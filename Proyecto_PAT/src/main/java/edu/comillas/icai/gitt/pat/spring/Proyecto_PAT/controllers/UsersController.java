package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    // Registrar usuario
    @PostMapping("/pistaPadel/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario register(@Valid @RequestBody Usuario usuario) {

        return usersService.register(usuario);

    }

    // Devuelve el usuario autenticado
    @GetMapping("/pistaPadel/auth/me")
    public Usuario me(Authentication authentication) {

        return usersService.me(authentication);

    }

    // GET lista usuarios (ADMIN)
    @GetMapping("/pistaPadel/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<Usuario> getUsers() {

        return usersService.getUsers();

    }

    // GET usuario por id (ADMIN)
    @GetMapping("/pistaPadel/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Usuario getUser(@PathVariable int userId) {

        return usersService.getUser(userId);

    }

    // Modificar usuario
    @PatchMapping("/pistaPadel/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Usuario modificarUser(@PathVariable int userId, @Valid @RequestBody Usuario usuario) {

        return usersService.modificarUser(userId, usuario);

    }
}