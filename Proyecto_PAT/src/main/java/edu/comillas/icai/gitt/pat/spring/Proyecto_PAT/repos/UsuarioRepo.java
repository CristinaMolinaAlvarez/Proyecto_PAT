package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepo extends CrudRepository<Usuario, Integer> {

    // login
    Optional<Usuario> findByEmailIgnoreCase(String email);

    // comprobar duplicado en registro
    boolean existsByEmail(String email);

    // métodos básicos
    Iterable<Usuario> findAll();
    Optional<Usuario> findById(Integer id);
    Usuario save(Usuario usuario);
    void deleteById(Integer id);
    boolean existsById(Integer id);

}