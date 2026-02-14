package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record Pista(
        int idPista,
        // No puede ser null ni vacío
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,
        @NotBlank(message = "La ubicación no puede estar vacía")
        String ubicacion,

        @Positive(message = "El precio debe ser mayor que 0")
        double precioHora,

        boolean activa,

        @NotNull(message = "La fecha es obligatoria")
        LocalDateTime fechaAlta
) {}