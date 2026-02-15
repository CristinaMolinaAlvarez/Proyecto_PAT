package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Usuario;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

//Esta clase responde a peticiones HTTP
@RestController
public class UsersController {

    // “BD” en memoria para Parte 1
    private final Map<Integer, Usuario> usuarios = new HashMap<>();

    public UsersController() {
        // OJO: pon usernames/emails que cuadren con vuestra ConfiguracionSeguridad
        usuarios.put(1, new Usuario(
                1, "Usuario", "Demo", "usuario@demo.com", "pass",
                "600000001", Rol.USER, LocalDateTime.now(), true
        ));
        usuarios.put(2, new Usuario(
                2, "Admin", "Demo", "admin@demo.com", "pass",
                "600000002", Rol.ADMIN, LocalDateTime.now(), true
        ));
    }


    // GET /pistaPadel/users (ADMIN)
    //Solo lo puede ejecutar alguien autorizado con rol de admin, si entramos con usuario 403
    @GetMapping("/pistaPadel/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listUsers() {
        return new ArrayList<>(usuarios.values());
    }


    // GET /pistaPadel/users/{userId} (ADMIN o usuario) pero con control extra
    // busca el usuario en el mapa y si no existe 404 y si el que llama no es admin y tampoco es el dueño 403
    @GetMapping("/pistaPadel/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Usuario getUser(Authentication auth, @PathVariable int userId) {
        Usuario u = usuarios.get(userId);
        if (u == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        if (!esAdmin(auth) && !esDueno(auth, u)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return u;
    }

    // PATCH /pistaPadel/users/{userId} (ADMIN o dueño)
    //  - 409 si email duplicado
    // 400 si viene email y no contiene @
    @PatchMapping("/pistaPadel/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Usuario patchUser(Authentication auth,
                             @PathVariable int userId,
                             @RequestBody UpdateUserRequest body) {

        Usuario actual = usuarios.get(userId);
        if (actual == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        if (!esAdmin(auth) && !esDueno(auth, actual)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // 400: si el email viene pero no tiene formato mínimo (la guía pide 400 “request inválida”)
        if (body.email() != null && !body.email().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 409: email duplicado (regla de negocio)
        if (body.email() != null && emailUsadoPorOtro(userId, body.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        // Construimos un nuevo Usuario (record = inmutable)
        Usuario actualizado = new Usuario(
                actual.idUsuario(),
                body.nombre() != null ? body.nombre() : actual.nombre(),
                body.apellidos() != null ? body.apellidos() : actual.apellidos(),
                body.email() != null ? body.email() : actual.email(),
                body.password() != null ? body.password() : actual.password(),
                body.telefono() != null ? body.telefono() : actual.telefono(),
                // rol: solo lo debería cambiar ADMIN (aquí lo mantenemos tal cual en Parte 1)
                actual.rol(),
                actual.fechaRegistro(),
                body.activo() != null ? body.activo() : actual.activo()
        );

        usuarios.put(userId, actualizado);
        return actualizado;
    }

    // DTO de PATCH
    // Campos opcionales: si vienen null no se tocan
    public record UpdateUserRequest(
            String nombre,
            String apellidos,
            String email,
            String password,
            String telefono,
            Boolean activo
    ) {}

    // helpers
    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean esDueno(Authentication auth, Usuario u) {
        // En Spring Security, auth.getName() suele ser el “username”.
        // Si nuestro username coincide con email, esto funciona directo.
        // Si no coincide, ajustamos este metodo.
        return auth.getName().equalsIgnoreCase(u.email());
    }

    private boolean emailUsadoPorOtro(int userId, String email) {
        return usuarios.values().stream()
                .anyMatch(u -> u.idUsuario() != userId && u.email().equalsIgnoreCase(email));
    }
}
