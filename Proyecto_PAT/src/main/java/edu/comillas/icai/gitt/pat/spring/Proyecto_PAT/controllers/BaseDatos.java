package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Pista;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Reserva;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Usuario;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaseDatos {

    // HashMap hardcodeado de usuarios
    private final Map<Integer, Usuario> usuarios = new HashMap<>(Map.of(
            1, new Usuario(1, "Carlos", "García", "admin@padel.com", "1234", "600000001", Rol.ADMIN, LocalDateTime.now(), true),
            2, new Usuario(2, "Ana", "López", "ana@padel.com", "1234", "600000002", Rol.USER, LocalDateTime.now(), true),
            3, new Usuario(3, "Mario", "Pérez", "mario@padel.com", "1234", "600000003", Rol.USER, LocalDateTime.now(), true)
    ));

    // HashMap hardcodeado de pistas
    private final Map<Integer, Pista> pistas = new HashMap<>(Map.of(
            1, new Pista(1, "Pista 1", "Interior", 20.0, true, LocalDateTime.now()),
            2, new Pista(2, "Pista 2", "Exterior", 18.0, true, LocalDateTime.now()),
            3, new Pista(3, "Pista 3", "Interior", 25.0, false, LocalDateTime.now())
    ));


    // HashMap de reservas
    private final Map<Integer, Reserva> reservas = new HashMap<>();

    // generador de idReserva
    private int nextReservaId = 1;

    public Map<Integer, Usuario> usuarios() {
        return usuarios;
    }

    public Map<Integer, Pista> pistas() {
        return pistas;
    }

    public Map<Integer, Reserva> reservas() {
        return reservas;
    }

    public int generarReservaId() {
        return nextReservaId++;
    }
}