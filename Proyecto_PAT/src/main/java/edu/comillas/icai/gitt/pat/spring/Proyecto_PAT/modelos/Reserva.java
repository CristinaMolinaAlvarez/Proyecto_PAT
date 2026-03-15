package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    // Muchas reservas pertenecen a un usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Muchas reservas pertenecen a una pista
    @ManyToOne
    @JoinColumn(name = "pista_id", nullable = false)
    private Pista pista;

    @NotNull(message = "La fechaReserva es obligatoria")
    private LocalDate fechaReserva;

    @NotNull(message = "La horaInicio es obligatoria")
    private LocalTime horaInicio;

    @Min(value = 1, message = "La duración debe ser >= 1 minuto")
    private int duracionMinutos;

    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private LocalDateTime fechaCreacion;

    public enum Estado {
        ACTIVA,
        CANCELADA
    }

    public Reserva() {}

    public Reserva(Integer idReserva, Usuario usuario, Pista pista, LocalDate fechaReserva, LocalTime horaInicio, int duracionMinutos, LocalTime horaFin, Estado estado, LocalDateTime fechaCreacion) {
        this.idReserva = idReserva;
        this.usuario = usuario;
        this.pista = pista;
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.duracionMinutos = duracionMinutos;
        this.horaFin = horaFin;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }
// GETTERS

    public Integer getIdReserva() {
        return idReserva;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Pista getPista() {
        return pista;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public Estado getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    // SETTERS

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setPista(Pista pista) {
        this.pista = pista;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
