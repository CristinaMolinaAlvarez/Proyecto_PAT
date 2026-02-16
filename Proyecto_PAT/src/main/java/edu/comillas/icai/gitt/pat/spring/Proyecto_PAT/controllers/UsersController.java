package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Usuario;
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
public class UsersController {

    // HashMap hardcodeado
    private final Map<Integer, Usuario> usuarios = new HashMap<>(Map.of(
            1, new Usuario(1, "Carlos", "García", "admin@padel.com", "1234", "600000001", Rol.ADMIN, LocalDateTime.now(), true),
            2, new Usuario(2, "Ana", "López", "ana@padel.com", "1234", "600000002", Rol.USER, LocalDateTime.now(), true),
            3, new Usuario(3, "Mario", "Pérez", "mario@padel.com", "1234", "600000003", Rol.USER, LocalDateTime.now(), true)
    ));

    // GET lista usuarios (ADMIN)
    @GetMapping("/pistaPadel/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Collection<Usuario> getUsers() {
        return usuarios.values();
    }

    // GET usuario por id (ADMIN)
    @GetMapping("/pistaPadel/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Usuario getUser(@PathVariable int userId) {

        Usuario usuario = usuarios.get(userId);

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
        if (!usuarios.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 409 si el email ya existe en otro usuario
        boolean emailExiste = usuarios.values().stream()
                .anyMatch(u -> u.email().equalsIgnoreCase(usuario.email())
                        && u.idUsuario() != userId);

        if (emailExiste) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        usuarios.put(userId, usuario);
        return usuario; // 200 por defecto si se modifica correctamente
    }
}