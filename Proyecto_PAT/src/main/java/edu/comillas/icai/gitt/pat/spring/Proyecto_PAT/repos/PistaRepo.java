package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Pista;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PistaRepo extends CrudRepository<Pista, Integer> {

    // pistas activas
    List<Pista> findByActivaTrue();

    // métodos básicos
    Iterable<Pista> findAll();
    Optional<Pista> findById(Integer id);
    Pista save(Pista pista);
    void deleteById(Integer id);
    boolean existsById(Integer id);

}