package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Reserva;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
public class ReservationsController {

    private final Map<Integer, Reserva> reservas = new HashMap<>();
    private int nextId = 1;

    // Crear reserva
    @PostMapping("/pistaPadel/reservations")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva crearReserva(@Valid @RequestBody Reserva reserva) {

        // 404 si la pista no existe (hardcode: 1..3)
        if (reserva.idPista() < 1 || reserva.idPista() > 3) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // Calcular horaFin
        var horaFin = reserva.horaInicio()
                .plusMinutes(reserva.duracionMinutos());

        // 409 si slot ocupado
        boolean ocupado = reservas.values().stream()
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

        Reserva creada = new Reserva(
                nextId++,
                reserva.idUsuario(),
                reserva.idPista(),
                reserva.fechaReserva(),
                reserva.horaInicio(),
                reserva.duracionMinutos(),
                horaFin,
                Reserva.Estado.ACTIVA,
                LocalDateTime.now()
        );

        reservas.put(creada.idReserva(), creada);
        return creada;
    }

    // Listar mis reservas
    @GetMapping("/pistaPadel/reservations")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Collection<Reserva> listarReservas(Authentication auth) {

        if (esAdmin(auth)) {
            return reservas.values();
        }

        int userId = resolverUserId(auth);
        return reservas.values().stream()
                .filter(r -> r.idUsuario() == userId)
                .toList();
    }

    // Obtener una reserva
    @GetMapping("/pistaPadel/reservations/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Reserva getReserva(Authentication auth, @PathVariable int id) {

        Reserva r = reservas.get(id);
        if (r == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!esAdmin(auth) && r.idUsuario() != resolverUserId(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return r;
    }

    // Cancelar reserva
    @DeleteMapping("/pistaPadel/reservations/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarReserva(Authentication auth, @PathVariable int id) {

        Reserva r = reservas.get(id);
        if (r == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!esAdmin(auth) && r.idUsuario() != resolverUserId(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        reservas.put(id,
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

    // Helpers
    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private int resolverUserId(Authentication auth) {
        return auth.getName().equals("usuario") ? 1 : 999;
    }
}

