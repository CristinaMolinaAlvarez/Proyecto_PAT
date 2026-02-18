package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Pista;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Reserva;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class ReservationsController {

    private final BaseDatos baseDatos;

    public ReservationsController(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    // Crear reserva
    @PostMapping("/pistaPadel/reservations")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva crearReserva(Authentication auth,
                                @Valid @RequestBody Reserva reserva) {

        int userId = resolverUserId(auth);

        // 404 si la pista no existe
        Pista pista = baseDatos.pistas().get(reserva.idPista());
        if (pista == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 409 si la pista está inactiva
        if (!pista.activa()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        // Calcular horaFin
        var horaFin = reserva.horaInicio()
                .plusMinutes(reserva.duracionMinutos());

        // 409 si el horario ya está ocupado
        boolean ocupado = baseDatos.reservas().values().stream()
                .filter(r -> r.idPista() == reserva.idPista())
                .filter(r -> r.fechaReserva().equals(reserva.fechaReserva()))
                .filter(r -> r.estado() == Reserva.Estado.ACTIVA)
                .anyMatch(r ->
                        r.horaInicio().isBefore(horaFin)
                                && reserva.horaInicio().isBefore(r.horaFin())
                );

        if (ocupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        int id = baseDatos.generarReservaId();

        Reserva creada = new Reserva(
                id,
                userId,
                reserva.idPista(),
                reserva.fechaReserva(),
                reserva.horaInicio(),
                reserva.duracionMinutos(),
                horaFin,
                Reserva.Estado.ACTIVA,
                LocalDateTime.now()
        );

        baseDatos.reservas().put(id, creada);
        return creada;
    }

    // Listar reservas
    @GetMapping("/pistaPadel/reservations")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Collection<Reserva> listarReservas(Authentication auth) {

        if (esAdmin(auth)) {
            return baseDatos.reservas().values();
        }

        int userId = resolverUserId(auth);

        return baseDatos.reservas().values().stream()
                .filter(r -> r.idUsuario() == userId)
                .toList();
    }

    // Obtener una reserva concreta
    @GetMapping("/pistaPadel/reservations/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Reserva getReserva(Authentication auth,
                              @PathVariable int id) {

        Reserva r = baseDatos.reservas().get(id);

        // 404 si no existe
        if (r == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 403 si no es suya y no es admin
        if (!esAdmin(auth) && r.idUsuario() != resolverUserId(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return r;
    }
    //Modificar reserva
    @PatchMapping("/pistaPadel/reservations/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Reserva reprogramarReserva(Authentication auth,
                                      @PathVariable int id,
                                      @Valid @RequestBody Reserva reservaActualizada) {

        Reserva existente = baseDatos.reservas().get(id);

        if (existente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!esAdmin(auth) && existente.idUsuario() != resolverUserId(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var nuevaHoraFin = reservaActualizada.horaInicio()
                .plusMinutes(reservaActualizada.duracionMinutos());

        Reserva nueva = new Reserva(
                id,
                existente.idUsuario(),
                existente.idPista(),
                reservaActualizada.fechaReserva(),
                reservaActualizada.horaInicio(),
                reservaActualizada.duracionMinutos(),
                nuevaHoraFin,
                Reserva.Estado.ACTIVA,
                existente.fechaCreacion()
        );

        baseDatos.reservas().put(id, nueva);

        return nueva;
    }


    // Cancelar reserva
    @DeleteMapping("/pistaPadel/reservations/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarReserva(Authentication auth,
                                @PathVariable int id) {

        Reserva r = baseDatos.reservas().get(id);

        // 404 si no existe
        if (r == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 403 si no es suya y no es admin
        if (!esAdmin(auth) && r.idUsuario() != resolverUserId(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        baseDatos.reservas().put(id,
                new Reserva(
                        r.idReserva(),
                        r.idUsuario(),
                        r.idPista(),
                        r.fechaReserva(),
                        r.horaInicio(),
                        r.duracionMinutos(),
                        r.horaFin(),
                        Reserva.Estado.CANCELADA,
                        r.fechaCreacion()
                )
        );
    }

    // Devuelve todas las reservas (uso interno)
    public List<Reserva> getAllInternal() {
        return new ArrayList<>(baseDatos.reservas().values());
    }

    // Comprueba si es ADMIN
    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Obtiene el id del usuario autenticado
    private int resolverUserId(Authentication auth) {

        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String email = auth.getName();

        Usuario usuario = baseDatos.usuarios().values().stream()
                .filter(u -> u.email().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return usuario.idUsuario();
    }
}