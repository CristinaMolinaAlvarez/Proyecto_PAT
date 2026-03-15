package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Pista;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.PistaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class CourtsService {

    @Autowired
    private PistaRepo pistaRepo;

    // Crear pista
    public Pista crearCourt(Pista pista) {

        // 409 si la pista ya existe
        if (pista.getIdPista() != null && pistaRepo.existsById(pista.getIdPista())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        pista.setFechaAlta(LocalDateTime.now());

        return pistaRepo.save(pista);
    }

    // Obtener lista de pistas
    public Iterable<Pista> getCourts() {
        return pistaRepo.findAll();
    }

    // Obtener una pista por id
    public Pista getCourt(int courtId) {

        return pistaRepo.findById(courtId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // Modificar pista
    public Pista modificarCourt(int courtId, Pista pista) {

        if (!pistaRepo.existsById(courtId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        pista.setIdPista(courtId);

        return pistaRepo.save(pista);
    }

    // Borrar pista
    public void borrarCourt(int courtId) {

        if (!pistaRepo.existsById(courtId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        pistaRepo.deleteById(courtId);
    }
}