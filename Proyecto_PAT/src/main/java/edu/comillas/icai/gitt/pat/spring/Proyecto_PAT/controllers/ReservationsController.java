package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Reserva;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.ReservaRequest;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services.ReservationsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReservationsController {

    private final ReservationsService reservationsService;

    public ReservationsController(ReservationsService reservationsService) {
        this.reservationsService = reservationsService;
    }


    // Crear reserva
    @PostMapping("/pistaPadel/reservations")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva crearReserva(Authentication auth, @Valid @RequestBody ReservaRequest reservaRequest) {

        return reservationsService.crearReserva(auth, reservaRequest);

    }


    // Listar reservas
    @GetMapping("/pistaPadel/reservations")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Reserva> listarReservas(Authentication auth) {

        return reservationsService.listarReservas(auth);

    }

    // Obtener una reserva concreta
    @GetMapping("/pistaPadel/reservations/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Reserva getReserva(Authentication auth, @PathVariable int id) {

        return reservationsService.getReserva(auth, id);

    }

    // Modificar reserva
    @PatchMapping("/pistaPadel/reservations/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Reserva reprogramarReserva(Authentication auth,
                                      @PathVariable int id,
                                      @Valid @RequestBody ReservaRequest reservaRequest) {

        return reservationsService.reprogramarReserva(auth, id, reservaRequest);

    }

    // Cancelar reserva
    @DeleteMapping("/pistaPadel/reservations/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarReserva(Authentication auth, @PathVariable int id) {

        reservationsService.cancelarReserva(auth, id);

    }
}