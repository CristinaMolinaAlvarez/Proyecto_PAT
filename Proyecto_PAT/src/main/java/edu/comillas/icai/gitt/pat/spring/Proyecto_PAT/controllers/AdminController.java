package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Reserva;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminController {

    // (ADMIN) Ver reservas de todos
    @GetMapping("/pistaPadel/admin/reservations")
    @PreAuthorize("hasRole('ADMIN')") // 401 si no autenticado, 403 si no es ADMIN
    public List<Reserva> getAllReservations(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer courtId,
            @RequestParam(required = false) Integer userId
    ) {
        return List.of(); // 200 OK
    }
}

