package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Pista;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    // Crear pista
    @PostMapping("/pistaPadel/courts")
    @ResponseStatus(HttpStatus.CREATED)
    public Pista crearCourt(@RequestBody Pista pista) {
        pistas.put(pista.idPista(), pista);
        return pista;
    }

    // Obtener lista de pistas
    @GetMapping("/pistaPadel/courts")
    public Collection<Pista> getCourts() {
        return pistas.values();
    }
    // Obtener una pista por id
    @GetMapping("/pistaPadel/courts/{courtId}")
    public Pista getCourt(@PathVariable int courtId) {
        return pistas.get(courtId);
    }

    // Modificar pista
    @PutMapping("/pistaPadel/courts/{courtId}")
    public Pista modificarCourt(@PathVariable int courtId,
                                @RequestBody Pista pista) {
        pistas.put(courtId, pista);
        return pista;
    }

    // Borrar pista
    @DeleteMapping("/pistaPadel/courts/{courtId}")
    public void borrarCourt(@PathVariable int courtId) {
        pistas.remove(courtId);
    }
}
