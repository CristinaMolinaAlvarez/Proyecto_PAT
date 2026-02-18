package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
public class UsersController {

    private final BaseDatos baseDatos;

    public UsersController(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    // Registrar usuario
    @PostMapping("/pistaPadel/auth/register")
    @ResponseStatus(HttpStatus.CREATED) // 201 si se crea correctamente
    public Usuario register(@Valid @RequestBody Usuario usuario) {
        // 400 si fallan validaciones (@Valid)

        // 409 si el email ya existe
        for (Usuario u : baseDatos.usuarios().values()) {
            if (u.email().equalsIgnoreCase(usuario.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

        // Rol USER por defecto al registrarse
        Usuario nuevo = new Usuario(
                usuario.idUsuario(),
                usuario.nombre(),
                usuario.apellidos(),
                usuario.email(),
                usuario.password(),
                usuario.telefono(),
                Rol.USER,
                LocalDateTime.now(),
                true
        );

        baseDatos.usuarios().put(nuevo.idUsuario(), nuevo);

        return nuevo; // 201 Created
    }

    // Devuelve el usuario autenticado
    @GetMapping("/pistaPadel/auth/me")
    public Usuario me(Authentication authentication) {

        // 401 si no está autenticado
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String email = authentication.getName(); // username = email

        for (Usuario u : baseDatos.usuarios().values()) {
            if (u.email().equalsIgnoreCase(email)) {
                return u; // 200 OK
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // GET lista usuarios (ADMIN)
    @GetMapping("/pistaPadel/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Collection<Usuario> getUsers() {
        return baseDatos.usuarios().values();
    }

    // GET usuario por id (ADMIN)
    @GetMapping("/pistaPadel/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Usuario getUser(@PathVariable int userId) {

        Usuario usuario = baseDatos.usuarios().get(userId);

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return usuario;
    }

    // Modificar usuario
    @PatchMapping("/pistaPadel/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')") // 401 si no autenticado y 403 si no es ADMIN
    public Usuario modificarUser(@PathVariable int userId,
                                 @Valid @RequestBody Usuario usuario) {
        // 400 si fallan validaciones (@Valid en el record Usuario)

        // 404 si el usuario no existe
        if (!baseDatos.usuarios().containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 409 si el email ya existe en otro usuario
        boolean emailExiste = false;
        for (Usuario u : baseDatos.usuarios().values()) {
            if (u.email().equalsIgnoreCase(usuario.email())
                    && u.idUsuario() != userId) {
                emailExiste = true;
                break;
            }
        }
        if (emailExiste) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        baseDatos.usuarios().put(userId, usuario);
        return usuario; // 200 por defecto si se modifica correctamente
    }
}