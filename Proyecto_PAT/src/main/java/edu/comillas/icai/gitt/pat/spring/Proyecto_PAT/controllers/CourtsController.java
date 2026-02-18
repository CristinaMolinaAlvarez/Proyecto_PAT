package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Pista;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
public class CourtsController {

    private final BaseDatos baseDatos;

    public CourtsController(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    // Crear pista
    @PostMapping("/pistaPadel/courts")
    @PreAuthorize("hasRole('ADMIN')") // 401 si no autenticado y 403 si no es ADMIN
    @ResponseStatus(HttpStatus.CREATED) // 201 si se crea correctamente
    public Pista crearCourt(@Valid @RequestBody Pista pista) {
        // 400 si fallan validaciones (@Valid en el record Pista)

        // 409 si la pista ya existe (regla de negocio)
        if (baseDatos.pistas().containsKey(pista.idPista())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        baseDatos.pistas().put(pista.idPista(), pista);
        return pista; // 201 cuando va bien
    }

    // Obtener lista de pistas
    @GetMapping("/pistaPadel/courts")
    public Collection<Pista> getCourts() {
        return baseDatos.pistas().values(); // 200 por defecto si va bien
    }

    // Obtener una pista por id
    @GetMapping("/pistaPadel/courts/{courtId}")
    public Pista getCourt(@PathVariable int courtId) {

        Pista pista = baseDatos.pistas().get(courtId);

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
        if (!baseDatos.pistas().containsKey(courtId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        baseDatos.pistas().put(courtId, pista);
        return pista; // 200 por defecto si se modifica correctamente
    }

    // Borrar pista
    @DeleteMapping("/pistaPadel/courts/{courtId}")
    @PreAuthorize("hasRole('ADMIN')") // 401 si no autenticado y 403 si no es ADMIN
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 si se elimina correctamente
    public void borrarCourt(@PathVariable int courtId) {

        // 404 si la pista no existe
        if (!baseDatos.pistas().containsKey(courtId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        baseDatos.pistas().remove(courtId); // 204 si va bien
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