package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    // Registrar usuario
    public Usuario register(Usuario usuario) {

        // 409 si el email ya existe
        if (usuarioRepo.existsByEmail(usuario.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        usuario.setRol(Rol.USER);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        return usuarioRepo.save(usuario);
    }

    // Devuelve el usuario autenticado
    public Usuario me(Authentication authentication) {

        // 401 si no está autenticado
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String email = authentication.getName();

        return usuarioRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // GET lista usuarios (ADMIN)
    public Iterable<Usuario> getUsers() {
        return usuarioRepo.findAll();
    }

    // GET usuario por id (ADMIN)
    public Usuario getUser(int userId) {

        return usuarioRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // Modificar usuario
    public Usuario modificarUser(int userId, Usuario usuario) {

        // 404 si el usuario no existe
        Usuario existente = usuarioRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 409 si el email ya existe en otro usuario
        Optional<Usuario> emailExistente = usuarioRepo.findByEmailIgnoreCase(usuario.getEmail());

        if (emailExistente.isPresent() && !emailExistente.get().getIdUsuario().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        // Actualizamos los campos modificables
        existente.setNombre(usuario.getNombre());
        existente.setApellidos(usuario.getApellidos());
        existente.setEmail(usuario.getEmail());
        existente.setTelefono(usuario.getTelefono());
        existente.setPassword(usuario.getPassword());

        // Guardamos el usuario actualizado
        return usuarioRepo.save(existente);
    }
}