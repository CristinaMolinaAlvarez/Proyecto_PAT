package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Pista;
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

    //Acceso directo a la base de datos
    private final BaseDatos baseDatos;

    public AvailabilityController(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
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

        //Devolvemos la disponibilidad de una pista si viene courtId
        if (courtId != null) {

            //404 si la pista no existe
            validarPistaExiste(courtId);

            return calculateAvailability(parsedDate, courtId);
        }

        // Si no viene courtId devolvemos para todas las pistas existentes
        List<Disponibilidad> resultado = new ArrayList<>();

        for (Integer idPista : baseDatos.pistas().keySet()) {
            resultado.add(calculateAvailability(parsedDate, idPista));
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

        //404 si la pista no existe
        validarPistaExiste(courtId);

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

        //Obtenemos todas las reservas de la base de datos
        List<Reserva> reservas = new ArrayList<>(baseDatos.reservas().values());

        // Recorre cada hora posible del dia y mira si existe alguna reserva activa
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

    private void validarPistaExiste(int courtId) {
        Pista pista = baseDatos.pistas().get(courtId);
        if (pista == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
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