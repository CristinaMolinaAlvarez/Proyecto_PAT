package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record Pista(
        int idPista,
        @NotBlank String nombre,
        @NotBlank String ubicacion,
        @Positive double precioHora,
        boolean activa,
        LocalDateTime fechaAlta
) {}