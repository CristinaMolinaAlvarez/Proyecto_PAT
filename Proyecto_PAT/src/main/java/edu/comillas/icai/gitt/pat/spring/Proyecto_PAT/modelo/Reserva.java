package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record Reserva(
        int idReserva,
        int idUsuario,
        int idPista,
        LocalDate fechaReserva,
        LocalTime horaInicio,
        int duracionMinutos,
        LocalTime horaFin,
        Estado estado,
        LocalDateTime fechaCreacion
) {

    public enum Estado {
        ACTIVA,
        CANCELADA
    }
}