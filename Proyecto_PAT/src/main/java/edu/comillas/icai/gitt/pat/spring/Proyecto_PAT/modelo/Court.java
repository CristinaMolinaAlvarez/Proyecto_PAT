package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class Court {

    private int idCourt;
    @NotBlank
    private String nombre;
    @NotBlank
    private String ubicacion;
    @Positive
    private double precioHora;
    private boolean activa = true;

    // constructor vacío (IMPORTANTE para JSON)
    public Court() {}

    public Court(int idCourt, String nombre, String ubicacion, double precioHora, boolean activa) {
        this.idCourt = idCourt;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precioHora = precioHora;
        this.activa = activa;
    }
    // getters y setters

    public int getIdCourt() {
        return idCourt;
    }

    public void setIdCourt(int idCourt) {
        this.idCourt = idCourt;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public double getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(double precioHora) {
        this.precioHora = precioHora;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}