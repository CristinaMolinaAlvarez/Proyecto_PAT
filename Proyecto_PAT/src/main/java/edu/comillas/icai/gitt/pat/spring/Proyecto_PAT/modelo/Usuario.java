package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record Usuario(

        Integer idUsuario,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
        String telefono,

        @NotNull(message = "El rol es obligatorio")
        Rol rol,

        LocalDateTime fechaRegistro,

        boolean activo

) {}