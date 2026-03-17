package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Pista;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Reserva;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.PistaRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.ReservaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AvailabilityService {

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityService.class);

    @Autowired
    private PistaRepo pistaRepo;

    @Autowired
    private ReservaRepo reservaRepo;

    // 1) Disponibilidad general, con o sin courtId
    public Object availability(String date, Integer courtId) {

        // Si no tiene date, responde 400
        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Convierte el String a LocalDate y si esta mal formado devuelve 400
        LocalDate parsedDate = parseDateOr400(date);

        // Devolvemos la disponibilidad de una pista si viene courtId
        if (courtId != null) {
            // 404 si la pista no existe
            validarPistaExiste(courtId);
            return calculateAvailability(parsedDate, courtId);
        }

        // Si no viene courtId devolvemos para todas las pistas existentes
        List<Disponibilidad> resultado = new ArrayList<>();

        Iterable<Pista> pistas = pistaRepo.findAll();

        for (Pista pista : pistas) {
            resultado.add(calculateAvailability(parsedDate, pista.getIdPista()));
        }

        return resultado;
    }

    // 2) Disponibilidad de una pista concreta
    public Disponibilidad availabilityCourt(int courtId, String date) {

        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        LocalDate parsedDate = parseDateOr400(date);

        // 404 si la pista no existe
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

        // Obtenemos todas las reservas de la base de datos
        List<Reserva> reservas = reservaRepo.findByPista_IdPistaAndFechaReserva(courtId, date);

        // Recorre cada hora posible del dia y mira si existe alguna reserva activa
        List<LocalTime> libres = new ArrayList<>();

        for (LocalTime slot : allSlots) {

            boolean ocupado = false;

            for (Reserva r : reservas) {
                if (r.getEstado() == Reserva.Estado.ACTIVA) {
                    if (!slot.isBefore(r.getHoraInicio()) && slot.isBefore(r.getHoraFin())) {
                        ocupado = true;
                        break;
                    }
                }
            }

            // Si no está ocupado lo devuelve a libres
            if (!ocupado) {
                libres.add(slot);
            }
        }

        // Devuelve la disponibilidad
        return new Disponibilidad(courtId, date, libres);
    }

    private void validarPistaExiste(int courtId) {
        Optional<Pista> pista = pistaRepo.findById(courtId);
        if (pista.isEmpty()) {
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