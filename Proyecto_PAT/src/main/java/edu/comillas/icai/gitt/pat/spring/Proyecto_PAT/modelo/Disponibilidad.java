package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record Disponibilidad(
        int idPista,
        LocalDate fecha,
        List<LocalTime> franjasDisponibles
) {}