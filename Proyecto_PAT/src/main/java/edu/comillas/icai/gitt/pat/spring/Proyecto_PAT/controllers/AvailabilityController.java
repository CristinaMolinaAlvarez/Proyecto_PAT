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

//Calcularemos qué horas están libres en una pista para un día, mirando las reservas activas
@RestController //Esta clase define endpoints REST y lo que se devuelve se convierte en JSON
public class AvailabilityController {

    //Acceso a reservas para poder calcular la disponibilidad
    private final ReservationsController reservationsController;

    public AvailabilityController(ReservationsController reservationsController) {
        this.reservationsController = reservationsController;
    }

    // 1) GET /pistaPadel/availability?date=...&courtId=...

    @GetMapping("/pistaPadel/availability")
    public Object availability(
            @RequestParam(required = false) String date, //podemos llamarlo para todas las pistas
            @RequestParam(required = false) Integer courtId //o para una en concreto
    ) {

        //Si no tiene date, responde 400
        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //Convierte el String a LocalDate y si esta mal formado devuelve 400
        LocalDate parsedDate = parseDateOr400(date);

        //Devolvemos la disponibilidad de una pista si viene courtDate
        if (courtId != null) {
            return calculateAvailability(parsedDate, courtId);
        }

        // Si no viene courtId devolvemos para todas
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

        //si piden la pista 99, 404 (No existe)
        if (courtId < 1 || courtId > 3) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return calculateAvailability(parsedDate, courtId);
    }

    // LÓGICA CENTRAL

    private Disponibilidad calculateAvailability(LocalDate date, int courtId) {

        // Horario del club
        // El horario será de 09:00-22:00
        // Los slots serán de 60 minutos
        List<LocalTime> allSlots = generateSlots(
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                60
        );

        //Pide todas las reservas que existen
        List<Reserva> reservas = reservationsController.getAllInternal();


        // Recorre cada hora posible del dia y mira si existe alguna reserva de la pista, de ese día, activa
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
            //Si no está ocupado lo devuelve a libres
            if (!ocupado) {
                libres.add(slot);
            }
        }
        //Devuelve la disponibilidad
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