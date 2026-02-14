package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Pista;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CourtsController {

    // HashMap hardcodeado para no tener que meterlo cada vez en Postman
    private final Map<Integer, Pista> pistas = new HashMap<>(Map.of(
            1, new Pista(1, "Pista 1", "Interior", 20.0, true, LocalDateTime.now()),
            2, new Pista(2, "Pista 2", "Exterior", 18.0, true, LocalDateTime.now()),
            3, new Pista(3, "Pista 3", "Interior", 25.0, false, LocalDateTime.now())
    ));

    // Crear pista padel
    @PostMapping("/pistaPadel/courts")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Pista crearCourt(@Valid @RequestBody Pista pista) {

        // Si no viene idPista, lo generamos nosotros
        Integer id = pista.idPista();
        if (id == null) {
            id = pistas.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
        }

        // Si no viene fechaAlta, la ponemos nosotros
        LocalDateTime fechaAlta = (pista.fechaAlta() != null) ? pista.fechaAlta() : LocalDateTime.now();

        Pista pistaFinal = new Pista(
                id,
                pista.nombre(),
                pista.ubicacion(),
                pista.precioHora(),
                pista.activa(),
                fechaAlta
        );

        // 409 si ya existe
        if (pistas.containsKey(pistaFinal.idPista())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        pistas.put(pistaFinal.idPista(), pistaFinal);
        return pistaFinal;
    }

    // Obtener lista de pistas
    @GetMapping("/pistaPadel/courts")
    public Collection<Pista> getCourts() {
        return pistas.values(); // 200 por defecto si va bien
    }

    // Obtener una pista por id
    @GetMapping("/pistaPadel/courts/{courtId}")
    public Pista getCourt(@PathVariable int courtId) {
        // obtenemos el id de la URL (PathVariable)
        Pista pista = pistas.get(courtId);

        // 404 si la pista no existe
        if (pista == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return pista; // 200 si existe
    }

    // Modificar pista
    @PatchMapping("/pistaPadel/courts/{courtId}")
    @PreAuthorize("hasRole('ADMIN')") // 401 si no autenticado y 403 si no es ADMIN
    public Pista modificarCourt(@PathVariable int courtId,
                                @Valid @RequestBody Pista pista) {
        // 400 si fallan validaciones (@Valid)

        // 404 si la pista no existe
        if (!pistas.containsKey(courtId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        LocalDateTime fechaAlta = (pista.fechaAlta() != null) ? pista.fechaAlta() : LocalDateTime.now();

        Pista pistaFinal = new Pista(
                courtId,
                pista.nombre(),
                pista.ubicacion(),
                pista.precioHora(),
                pista.activa(),
                fechaAlta
        );

        pistas.put(courtId, pistaFinal);
        return pistaFinal;
    }

    // Borrar pista
    @DeleteMapping("/pistaPadel/courts/{courtId}")
    @PreAuthorize("hasRole('ADMIN')") // 401 si no autenticado y 403 si no es ADMIN
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 si se elimina correctamente
    public void borrarCourt(@PathVariable int courtId) {
        // 404 si la pista no existe
        if (!pistas.containsKey(courtId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        pistas.remove(courtId); // 204 si va bien
    }
}

/*
Resumen:

200 → por defecto en GET y PATCH
201 → con @ResponseStatus(HttpStatus.CREATED)
204 → con @ResponseStatus(HttpStatus.NO_CONTENT)

400 → lo genera @Valid
401 → lo genera Spring Security si no hay login
403 → lo genera Spring Security si no es ADMIN
404 → lo lanzamos nosotros con ResponseStatusException
409 → conflicto manual en POST

 */