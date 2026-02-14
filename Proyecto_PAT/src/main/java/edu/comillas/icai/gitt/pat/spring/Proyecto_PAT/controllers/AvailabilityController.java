package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Reserva;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AvailabilityController {

    private final ReservationsController reservationsController;

    public AvailabilityController(ReservationsController reservationsController) {
        this.reservationsController = reservationsController;
    }

    // 1) GET /pistaPadel/availability?date=...&courtId=...

    @GetMapping("/pistaPadel/availability")
    public Object availability(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer courtId
    ) {

        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        LocalDate parsedDate = parseDateOr400(date);

        if (courtId != null) {
            return calculateAvailability(parsedDate, courtId);
        }

        // Si no viene courtId → devolvemos para todas (1..3 hardcode Parte 1)
        List<Disponibilidad> resultado = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            resultado.add(calculateAvailability(parsedDate, i));
        }

        return resultado;
    }

    // 2) GET /pistaPadel/courts/{courtId}/availability?date=...

    @GetMapping("/pistaPadel/courts/{courtId}/availability")
    public Disponibilidad availabilityCourt(
            @PathVariable int courtId,
            @RequestParam(required = false) String date
    ) {

        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        LocalDate parsedDate = parseDateOr400(date);

        if (courtId < 1 || courtId > 3) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return calculateAvailability(parsedDate, courtId);
    }

    // LÓGICA DE CÁLCULO (esto es lo importante)

    private Disponibilidad calculateAvailability(LocalDate date, int courtId) {

        // Horario del club (definido por aplicación) :contentReference[oaicite:3]{index=3}
        List<LocalTime> allSlots = generateSlots(
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                60
        );

        List<Reserva> reservas = reservationsController.getAllInternal();
        // Este método interno lo tienes que exponer en ReservationsController

        List<LocalTime> libres = new ArrayList<>();

        for (LocalTime slot : allSlots) {

            boolean ocupado = reservas.stream()
                    .filter(r -> r.idPista() == courtId)
                    .filter(r -> r.fechaReserva().equals(date))
                    .filter(r -> r.estado() == Reserva.Estado.ACTIVA)
                    .anyMatch(r ->
                            !slot.isBefore(r.horaInicio())
                                    && slot.isBefore(r.horaFin())
                    );

            if (!ocupado) {
                libres.add(slot);
            }
        }

        return new Disponibilidad(courtId, date, libres);
    }

    private LocalDate parseDateOr400(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private List<LocalTime> generateSlots(LocalTime start, LocalTime end, int minutes) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime t = start;

        while (t.plusMinutes(minutes).compareTo(end) <= 0) {
            slots.add(t);
            t = t.plusMinutes(minutes);
        }

        return slots;
    }
}