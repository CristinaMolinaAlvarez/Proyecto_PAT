package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.unit;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Pista;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.PistaRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services.CourtsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourtsServiceUnitTest {

    @Mock
    private PistaRepo pistaRepo;

    @InjectMocks
    private CourtsService courtsService;

    @Test
    void getCourtWithInvalidIdShouldThrowNotFound() {
        when(pistaRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> courtsService.getCourt(99));
    }

    @Test
    void modifyCourtWithInvalidIdShouldThrowNotFound() {
        Pista pista = new Pista();
        pista.setNombre("Pista");
        pista.setUbicacion("Interior");
        pista.setPrecioHora(20);
        pista.setActiva(true);

        when(pistaRepo.existsById(99)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> courtsService.modificarCourt(99, pista));
    }

    @Test
    void deleteCourtWithInvalidIdShouldThrowNotFound() {
        when(pistaRepo.existsById(99)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> courtsService.borrarCourt(99));
    }
}