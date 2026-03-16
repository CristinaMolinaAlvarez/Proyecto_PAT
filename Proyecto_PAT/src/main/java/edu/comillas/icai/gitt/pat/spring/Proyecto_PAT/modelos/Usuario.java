package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(message = "La contraseña es obligatoria")
        @Column(nullable = false)
        private String password;

        @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
        private String telefono;

        @Enumerated(EnumType.STRING)
        private Rol rol;

        private LocalDateTime fechaRegistro;

        private Boolean activo;

        // Un usuario puede tener muchas reservas
        // Se ignora en JSON para evitar bucle infinito entre Usuario/Pista y Reserva
        @JsonIgnore
        // Un usuario puede tener muchas reservas
        // Si se borra un usuario se borran sus reservas
        @OneToMany(mappedBy = "usuario")
        @OnDelete(action = OnDeleteAction.CASCADE)
        private List<Reserva> reservas;

        public Usuario() {}

        public Usuario(Integer idUsuario, String nombre, String apellidos, String email, String password, String telefono, Rol rol, LocalDateTime fechaRegistro, Boolean activo) {
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

        // GETTERS

        public Integer getIdUsuario() {
                return idUsuario;
        }

        public String getNombre() {
                return nombre;
        }

        public String getApellidos() {
                return apellidos;
        }

        public String getEmail() {
                return email;
        }

        public String getPassword() {
                return password;
        }

        public String getTelefono() {
                return telefono;
        }

        public Rol getRol() {
                return rol;
        }

        public LocalDateTime getFechaRegistro() {
                return fechaRegistro;
        }

        public Boolean getActivo() {
                return activo;
        }

        public List<Reserva> getReservas() {
                return reservas;
        }

        // SETTERS

        public void setIdUsuario(Integer idUsuario) {
                this.idUsuario = idUsuario;
        }

        public void setNombre(String nombre) {
                this.nombre = nombre;
        }

        public void setApellidos(String apellidos) {
                this.apellidos = apellidos;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public void setTelefono(String telefono) {
                this.telefono = telefono;
        }

        public void setRol(Rol rol) {
                this.rol = rol;
        }

        public void setFechaRegistro(LocalDateTime fechaRegistro) {
                this.fechaRegistro = fechaRegistro;
        }

        public void setActivo(Boolean activo) {
                this.activo = activo;
        }

        public void setReservas(List<Reserva> reservas) {
                this.reservas = reservas;
        }
}