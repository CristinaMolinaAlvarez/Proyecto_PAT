package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.unit;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.*;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.PistaRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.ReservaRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services.ReservationsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationsServiceUnitTest {

    @Mock
    private ReservaRepo reservaRepo;

    @Mock
    private UsuarioRepo usuarioRepo;

    @Mock
    private PistaRepo pistaRepo;

    @InjectMocks
    private ReservationsService reservationsService;

    @Test
    void createReservationWithoutAuthenticationShouldThrowUnauthorized() {
        ReservaRequest request = new ReservaRequest();
        request.setIdPista(1);
        request.setFechaReserva(LocalDate.of(2026, 2, 14));
        request.setHoraInicio(LocalTime.of(10, 0));
        request.setDuracionMinutos(60);

        assertThrows(ResponseStatusException.class, () -> reservationsService.crearReserva(null, request));
    }

    @Test
    void createReservationWithNonExistingCourtShouldThrowNotFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuario@test.com");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEmail("usuario@test.com");
        usuario.setRol(Rol.USER);

        when(usuarioRepo.findByEmailIgnoreCase("usuario@test.com")).thenReturn(Optional.of(usuario));
        when(pistaRepo.findById(1)).thenReturn(Optional.empty());

        ReservaRequest request = new ReservaRequest();
        request.setIdPista(1);
        request.setFechaReserva(LocalDate.of(2026, 2, 14));
        request.setHoraInicio(LocalTime.of(10, 0));
        request.setDuracionMinutos(60);

        assertThrows(ResponseStatusException.class, () -> reservationsService.crearReserva(auth, request));
    }

    @Test
    void createReservationWithInactiveCourtShouldThrowConflict() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuario@test.com");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEmail("usuario@test.com");
        usuario.setRol(Rol.USER);

        Pista pista = new Pista();
        pista.setIdPista(1);
        pista.setActiva(false);

        when(usuarioRepo.findByEmailIgnoreCase("usuario@test.com")).thenReturn(Optional.of(usuario));
        when(pistaRepo.findById(1)).thenReturn(Optional.of(pista));

        ReservaRequest request = new ReservaRequest();
        request.setIdPista(1);
        request.setFechaReserva(LocalDate.of(2026, 2, 14));
        request.setHoraInicio(LocalTime.of(10, 0));
        request.setDuracionMinutos(60);

        assertThrows(ResponseStatusException.class, () -> reservationsService.crearReserva(auth, request));
    }

    @Test
    void createReservationWithOverlappingSlotShouldThrowConflict() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuario@test.com");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEmail("usuario@test.com");
        usuario.setRol(Rol.USER);

        Pista pista = new Pista();
        pista.setIdPista(1);
        pista.setActiva(true);

        Reserva reservaExistente = new Reserva();
        reservaExistente.setIdReserva(1);
        reservaExistente.setEstado(Reserva.Estado.ACTIVA);
        reservaExistente.setHoraInicio(LocalTime.of(10, 0));
        reservaExistente.setHoraFin(LocalTime.of(11, 0));

        when(usuarioRepo.findByEmailIgnoreCase("usuario@test.com")).thenReturn(Optional.of(usuario));
        when(pistaRepo.findById(1)).thenReturn(Optional.of(pista));
        when(reservaRepo.findByPista_IdPistaAndFechaReserva(eq(1), any(LocalDate.class)))
                .thenReturn(List.of(reservaExistente));

        ReservaRequest request = new ReservaRequest();
        request.setIdPista(1);
        request.setFechaReserva(LocalDate.of(2026, 2, 14));
        request.setHoraInicio(LocalTime.of(10, 30));
        request.setDuracionMinutos(60);

        assertThrows(ResponseStatusException.class, () -> reservationsService.crearReserva(auth, request));
    }
}