package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record Reserva(
        int idReserva,
        @Positive(message = "El idUsuario debe ser > 0")
        int idUsuario,
        @Positive(message = "El idPista debe ser > 0")
        int idPista,
        @NotNull(message = "La fechaReserva es obligatoria")
        LocalDate fechaReserva,
        @NotNull(message = "La horaInicio es obligatoria")
        LocalTime horaInicio,
        @Min(value = 1, message = "La duración debe ser >= 1 minuto")
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

// COMENTARIO