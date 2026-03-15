package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Usuario {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer idUsuario;

        @NotBlank(message = "El nombre es obligatorio")
        @Column(nullable = false)
        private String nombre;

        @NotBlank(message = "Los apellidos son obligatorios")
        @Column(nullable = false)
        private String apellidos;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        @Column(nullable = false, unique = true)
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Column(nullable = false)
        private String password;

        @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
        private String telefono;

        @NotNull(message = "El rol es obligatorio")
        @Enumerated(EnumType.STRING)
        private Rol rol;

        private LocalDateTime fechaRegistro;

        private boolean activo;

        // Un usuario puede tener muchas reservas
        // Si se borra un usuario se borran sus reservas
        @OneToMany(mappedBy = "usuario")
        @OnDelete(action = OnDeleteAction.CASCADE)
        private List<Reserva> reservas;

        public Usuario() {}

        public Usuario(Integer idUsuario, String nombre, String apellidos, String email, String password, String telefono, Rol rol, LocalDateTime fechaRegistro, boolean activo) {
                this.idUsuario = idUsuario;
                this.nombre = nombre;
                this.apellidos = apellidos;
                this.email = email;
                this.password = password;
                this.telefono = telefono;
                this.rol = rol;
                this.fechaRegistro = fechaRegistro;
                this.activo = activo;
        }

        public Integer getIdUsuario() {
                return idUsuario;
        }

        public String getEmail() {
                return email;
        }

        public Rol getRol() {
                return rol;
        }

}