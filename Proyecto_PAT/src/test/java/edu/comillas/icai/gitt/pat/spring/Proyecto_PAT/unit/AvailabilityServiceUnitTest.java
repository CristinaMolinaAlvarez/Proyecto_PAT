package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.unit;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Pista;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.PistaRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.ReservaRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceUnitTest {

    @Mock
    private PistaRepo pistaRepo;

    @Mock
    private ReservaRepo reservaRepo;

    @InjectMocks
    private AvailabilityService availabilityService;

    @Test
    void availabilityWithoutDateShouldThrowBadRequest() {
        assertThrows(ResponseStatusException.class, () -> availabilityService.availability(null, null));
    }

    @Test
    void availabilityCourtWithInvalidIdShouldThrowNotFound() {
        when(pistaRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> availabilityService.availabilityCourt(99, "2026-02-14"));
    }

    @Test
    void availabilityCourtShouldReturnDisponibilidad() {
        Pista pista = new Pista();
        pista.setIdPista(1);

        when(pistaRepo.findById(1)).thenReturn(Optional.of(pista));
        when(reservaRepo.findByPista_IdPistaAndFechaReserva(1, LocalDate.of(2026, 2, 14)))
                .thenReturn(List.of());

        Disponibilidad disponibilidad = availabilityService.availabilityCourt(1, "2026-02-14");

        assertNotNull(disponibilidad);
        assertEquals(1, disponibilidad.idPista());
        assertEquals(LocalDate.of(2026, 2, 14), disponibilidad.fecha());
        assertFalse(disponibilidad.franjasDisponibles().isEmpty());
    }
}