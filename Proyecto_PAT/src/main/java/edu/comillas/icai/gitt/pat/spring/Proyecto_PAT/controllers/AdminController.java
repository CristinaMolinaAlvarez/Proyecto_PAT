package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Reserva;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.time.LocalDate;

//Nos permite que un ADMIN vea todas las reservas del sistema, con filtros opcionales
@RestController
public class AdminController {

    // crea reservations controller, admin controller y le pasa referencia a reservationscontroller, admin necesita acceder a la memoria
    private final ReservationsController reservationsController;

    public AdminController(ReservationsController reservationsController) {
        this.reservationsController = reservationsController;
    }

    // (ADMIN) Ver reservas de todos
    //Endpoint principal
    @GetMapping("/pistaPadel/admin/reservations")
    // Antes de que se ejecute hay que ver si la persona esta logueada y si eres admin o no (gestionado por SpringSecurity)
    @PreAuthorize("hasRole('ADMIN')") // 401 si no autenticado, 403 si no es ADMIN
    public List<Reserva> getAllReservations(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer courtId,
            @RequestParam(required = false) Integer userId
    ) {
        //Observamos todas las reservas
        List<Reserva> reservas = reservationsController.getAllInternal();

        // Filtro por date si viene
        if (date != null) {
            try {
                LocalDate parsedDate = LocalDate.parse(date);
                reservas = reservas.stream()
                        .filter(r -> r.fechaReserva().equals(parsedDate))
                        .toList();
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
        // Filtro por courtId si viene
        if (courtId != null) {
            reservas = reservas.stream()
                    .filter(r -> r.idPista() == courtId)
                    .toList();
        }

        // Filtro por userId si viene
        if (userId != null) {
            reservas = reservas.stream()
                    .filter(r -> r.idUsuario() == userId)
                    .toList();
        }

        return reservas; // 200
    }
    }

