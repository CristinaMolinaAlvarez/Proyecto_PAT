package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Reserva;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class ReservationsController {

    // Simulación simple:
    // - USER autenticado ("usuario") corresponde a idUsuario = 1
    // - ADMIN ("admin") puede ver todo
    private static final int USER_ID_SIMULADO = 1;

    private final AtomicInteger nextId = new AtomicInteger(100);

    private final Map<Integer, Reserva> reservas = new HashMap<>();

    public ReservationsController() {
        // Ejemplo de reserva ocupando 10:00-11:00 en pista 1 el día de hoy
        var hoy = java.time.LocalDate.now();
        reservas.put(1, new Reserva(
                1,
                USER_ID_SIMULADO,
                1,
                hoy,
                LocalTime.of(10, 0),
                60,
                LocalTime.of(11, 0),
                Reserva.Estado.ACTIVA,
                LocalDateTime.now()
        ));
    }

    // (USER) Crear reserva
    @PostMapping("/pistaPadel/reservations")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva crearReserva(@Valid @RequestBody Reserva body) {
        // 400 -> @Valid
        // 404 (pista no existe) -> lo haremos desde Availability/Courts al integrar
        if (body.idPista() < 1 || body.idPista() > 3) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pista no existe");
        }

        // calculamos horaFin, estado, fechaCreacion
        LocalTime horaFin = body.horaInicio().plusMinutes(body.duracionMinutos());

        // 409 si slot ocupado (colisión en misma pista y misma fecha y solapamiento de horas)
        boolean ocupado = reservas.values().stream()
                .filter(r -> r.idPista() == body.idPista())
                .filter(r -> r.fechaReserva().equals(body.fechaReserva()))
                .filter(r -> r.estado() == Reserva.Estado.ACTIVA)
                .anyMatch(r -> solapa(r.horaInicio(), r.horaFin(), body.horaInicio(), horaFin));

        if (ocupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot ocupado");
        }

        int id = nextId.getAndIncrement();
        Reserva creada = new Reserva(
                id,
                body.idUsuario(),
                body.idPista(),
                body.fechaReserva(),
                body.horaInicio(),
                body.duracionMinutos(),
                horaFin,
                Reserva.Estado.ACTIVA,
                LocalDateTime.now()
        );

        reservas.put(id, creada);
        return creada;
    }

    // (USER) Listar mis reservas
    @GetMapping("/pistaPadel/reservations")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Reserva> listarMisReservas(Authentication auth,
                                           @RequestParam(required = false) String from,
                                           @RequestParam(required = false) String to) {
        int userId = resolveUserId(auth);

        return reservas.values().stream()
                .filter(r -> auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                        || r.idUsuario() == userId)
                .sorted(Comparator.comparing(Reserva::fechaReserva).thenComparing(Reserva::horaInicio))
                .collect(Collectors.toList());
    }

    // (USER dueño o ADMIN) Obtener una reserva
    @GetMapping("/pistaPadel/reservations/{reservationId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Reserva getReserva(Authentication auth, @PathVariable int reservationId) {
        Reserva r = reservas.get(reservationId);
        if (r == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        if (!esAdmin(auth) && r.idUsuario() != resolveUserId(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return r;
    }

    // (USER dueño o ADMIN) Cancelar reserva
    @DeleteMapping("/pistaPadel/reservations/{reservationId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarReserva(Authentication auth, @PathVariable int reservationId) {
        Reserva r = reservas.get(reservationId);
        if (r == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        if (!esAdmin(auth) && r.idUsuario() != resolveUserId(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // Ejemplo de política opcional -> 409 si ya está cancelada
        if (r.estado() == Reserva.Estado.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya cancelada");
        }

        Reserva cancelada = new Reserva(
                r.idReserva(), r.idUsuario(), r.idPista(), r.fechaReserva(),
                r.horaInicio(), r.duracionMinutos(), r.horaFin(),
                Reserva.Estado.CANCELADA, r.fechaCreacion()
        );

        reservas.put(reservationId, cancelada);
    }

    // (USER dueño o ADMIN) Reprogramar/cambiar reserva
    @PatchMapping("/pistaPadel/reservations/{reservationId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Reserva reprogramarReserva(Authentication auth,
                                      @PathVariable int reservationId,
                                      @Valid @RequestBody Reserva body) {
        Reserva actual = reservas.get(reservationId);
        if (actual == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        if (!esAdmin(auth) && actual.idUsuario() != resolveUserId(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // Validamos pista existente (stub)
        if (body.idPista() < 1 || body.idPista() > 3) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pista no existe");
        }

        LocalTime nuevaFin = body.horaInicio().plusMinutes(body.duracionMinutos());

        // 409 si nuevo slot ocupado (ignorando la propia reserva)
        boolean ocupado = reservas.values().stream()
                .filter(r -> r.idReserva() != reservationId)
                .filter(r -> r.idPista() == body.idPista())
                .filter(r -> r.fechaReserva().equals(body.fechaReserva()))
                .filter(r -> r.estado() == Reserva.Estado.ACTIVA)
                .anyMatch(r -> solapa(r.horaInicio(), r.horaFin(), body.horaInicio(), nuevaFin));

        if (ocupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nuevo slot ocupado");
        }

        Reserva actualizada = new Reserva(
                reservationId,
                actual.idUsuario(), // mantenemos dueño
                body.idPista(),
                body.fechaReserva(),
                body.horaInicio(),
                body.duracionMinutos(),
                nuevaFin,
                Reserva.Estado.ACTIVA,
                actual.fechaCreacion()
        );

        reservas.put(reservationId, actualizada);
        return actualizada;
    }


    private boolean solapa(LocalTime startA, LocalTime endA, LocalTime startB, LocalTime endB) {
        // Solapan si A empieza antes de que B termine y B empieza antes de que A termine
        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private int resolveUserId(Authentication auth) {
        // Si en vuestra seguridad el username es "usuario" -> id 1
        // Si es "admin" -> id 999 (da igual porque admin pasa por esAdmin)
        String username = auth.getName();
        if ("usuario".equals(username)) return USER_ID_SIMULADO;
        return 999;
    }
}
