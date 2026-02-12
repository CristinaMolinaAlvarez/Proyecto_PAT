package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record Usuario(
        int idUsuario,
        @NotBlank String nombre,
        @NotBlank String apellidos,
        @Email @NotBlank String email,
        @NotBlank String password,
        String telefono,
        Rol rol,
        LocalDateTime fechaRegistro,
        boolean activo
) {}