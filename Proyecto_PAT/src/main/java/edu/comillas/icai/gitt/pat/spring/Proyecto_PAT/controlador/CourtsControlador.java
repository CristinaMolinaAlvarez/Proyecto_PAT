package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controlador;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Court;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CourtsControlador {

    // HashMap hardcodeado para no tener que meterlo cada vez en Postman
    private final Map<Integer, Court> courts = new HashMap<>(Map.of(
            1, new Court(1, "Pista 1", "Interior", 20.0, true),
            2, new Court(2, "Pista 2", "Exterior", 18.0, true),
            3, new Court(3, "Pista 3", "Interior", 25.0, false)
    ));

    // Crear pista
    @PostMapping("/pistaPadel/courts")
    @ResponseStatus(HttpStatus.CREATED)
    public Court crearCourt(@RequestBody Court court) {
        courts.put(court.getIdCourt(), court);
        return court;
    }

    // Obtener lista de pistas
    @GetMapping("/pistaPadel/courts")
    public Collection<Court> getCourts() {
        return courts.values();
    }
    // Obtener una pista por id
    @GetMapping("/pistaPadel/courts/{idCourt}")
    public Court getCourt(@PathVariable int idCourt) {
        return courts.get(idCourt);
    }

    // Modificar pista
    @PutMapping("/pistaPadel/courts/{idCourt}")
    public Court modificarCourt(@PathVariable int idCourt,
                                @RequestBody Court court) {
        courts.put(idCourt, court);
        return court;
    }

    // Borrar pista
    @DeleteMapping("/pistaPadel/courts/{idCourt}")
    public void borrarCourt(@PathVariable int idCourt) {
        courts.remove(idCourt);
    }
}
