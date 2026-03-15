package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Pista;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services.CourtsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class CourtsController {

    private final CourtsService courtsService;

    public CourtsController(CourtsService courtsService) {
        this.courtsService = courtsService;
    }

    // Crear pista
    @PostMapping("/pistaPadel/courts")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Pista crearCourt(@Valid @RequestBody Pista pista) {

        return courtsService.crearCourt(pista);

    }

    // Obtener lista de pistas
    @GetMapping("/pistaPadel/courts")
    public Iterable<Pista> getCourts() {

        return courtsService.getCourts();

    }

    // Obtener una pista por id
    @GetMapping("/pistaPadel/courts/{courtId}")
    public Pista getCourt(@PathVariable int courtId) {

        return courtsService.getCourt(courtId);

    }

    // Modificar pista
    @PatchMapping("/pistaPadel/courts/{courtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Pista modificarCourt(@PathVariable int courtId, @Valid @RequestBody Pista pista) {

        return courtsService.modificarCourt(courtId, pista);

    }

    // Borrar pista
    @DeleteMapping("/pistaPadel/courts/{courtId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrarCourt(@PathVariable int courtId) {

        courtsService.borrarCourt(courtId);

    }
}