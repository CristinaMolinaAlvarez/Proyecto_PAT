package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.unit;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services.UsersService;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.util.Hashing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceUnitTest {

    @Mock
    private UsuarioRepo usuarioRepo;

    @Mock
    private Hashing hashing;

    @InjectMocks
    private UsersService usersService;

    @Test
    void registerWithExistingEmailShouldThrowConflict() {
        Usuario usuario = new Usuario();
        usuario.setEmail("usuario@test.com");
        usuario.setPassword("clave");

        when(usuarioRepo.existsByEmail("usuario@test.com")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> usersService.register(usuario));
    }

    @Test
    void meWithoutAuthenticationShouldThrowUnauthorized() {
        assertThrows(ResponseStatusException.class, () -> usersService.me(null));
    }

    @Test
    void meWithUnknownUserShouldThrowNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("desconocido@test.com");
        when(usuarioRepo.findByEmailIgnoreCase("desconocido@test.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> usersService.me(authentication));
    }
}