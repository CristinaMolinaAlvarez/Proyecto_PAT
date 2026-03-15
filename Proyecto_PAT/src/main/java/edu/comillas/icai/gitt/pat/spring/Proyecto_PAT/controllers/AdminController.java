package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Reserva;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Permite que un ADMIN vea todas las reservas del sistema con filtros opcionales
@RestController
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // (ADMIN) Ver reservas de todos
    @GetMapping("/pistaPadel/admin/reservations")

    // Spring Security valida autenticación y rol
    // 401 si no autenticado, 403 si no es ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reserva> getAllReservations(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer courtId,
            @RequestParam(required = false) Integer userId
    ) {

        return adminService.getAllReservations(date, courtId, userId);

    }
}