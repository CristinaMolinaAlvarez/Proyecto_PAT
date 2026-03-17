package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Reserva;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.ReservaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private ReservaRepo reservaRepo;

    // (ADMIN) Ver reservas de todos
    public List<Reserva> getAllReservations(String date, Integer courtId, Integer userId) {

        // Observamos todas las reservas
        List<Reserva> reservas = new ArrayList<>();
        reservaRepo.findAll().forEach(reservas::add);

        // Filtro por date si viene
        if (date != null) {
            try {
                LocalDate parsedDate = LocalDate.parse(date);

                List<Reserva> filtradas = new ArrayList<>();
                for (Reserva r : reservas) {
                    if (r.getFechaReserva().equals(parsedDate)) {
                        filtradas.add(r);
                    }
                }
                reservas = filtradas;

            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        // Filtro por courtId si viene
        if (courtId != null) {
            List<Reserva> filtradas = new ArrayList<>();
            for (Reserva r : reservas) {
                if (r.getPista().getIdPista().equals(courtId)) {
                    filtradas.add(r);
                }
            }
            reservas = filtradas;
        }

        // Filtro por userId si viene
        if (userId != null) {
            List<Reserva> filtradas = new ArrayList<>();
            for (Reserva r : reservas) {
                if (r.getUsuario().getIdUsuario().equals(userId)) {
                    filtradas.add(r);
                }
            }
            reservas = filtradas;
        }

        return reservas;
    }
}