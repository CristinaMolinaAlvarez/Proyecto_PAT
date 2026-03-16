package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaRequest {

    @NotNull
    private Long idPista;

    @NotNull
    private LocalDate fechaReserva;

    @NotNull
    private LocalTime horaInicio;

    //Mínimo 30 minutos
    @Min(30)

    //Máximo 180 minutos
    @Max(180)
    private int duracionMinutos;

    public Long getIdPista() {
        return idPista;
    }

    public void setIdPista(Long idPista) {
        this.idPista = idPista;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
}