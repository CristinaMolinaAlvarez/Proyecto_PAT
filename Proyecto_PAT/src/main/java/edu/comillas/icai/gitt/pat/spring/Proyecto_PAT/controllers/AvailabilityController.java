package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services.AvailabilityService;
import org.springframework.web.bind.annotation.*;

// Calcula qué horas están libres en una pista para un día
@RestController
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    // 1) GET /pistaPadel/availability?date=...&courtId=...
    @GetMapping("/pistaPadel/availability")
    public Object availability(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer courtId
    ) {

        return availabilityService.availability(date, courtId);

    }

    // 2) GET /pistaPadel/courts/{courtId}/availability?date=...
    @GetMapping("/pistaPadel/courts/{courtId}/availability")
    public Disponibilidad availabilityCourt(
            @PathVariable int courtId,
            @RequestParam(required = false) String date
    ) {

        return availabilityService.availabilityCourt(courtId, date);

    }
}