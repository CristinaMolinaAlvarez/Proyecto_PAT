package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
// Esta clase no es una entidad JPA porque no se guarda en base de datos.
// Solo se usa como DTO para devolver en la API la disponibilidad calculada de una pista en un día.
public record Disponibilidad(
        int idPista,
        LocalDate fecha,
        List<LocalTime> franjasDisponibles
) {}