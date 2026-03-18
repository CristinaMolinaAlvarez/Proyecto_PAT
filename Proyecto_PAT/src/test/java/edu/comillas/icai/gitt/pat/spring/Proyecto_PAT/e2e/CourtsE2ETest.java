package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.e2e;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CourtsE2ETest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararAdmin() {
        usuarioRepo.findByEmailIgnoreCase("admin@test.com").ifPresent(usuarioRepo::delete);

        Usuario admin = new Usuario();
        admin.setNombre("Admin");
        admin.setApellidos("Prueba");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("clave"));
        admin.setRol(Rol.ADMIN);
        admin.setActivo(true);
        usuarioRepo.save(admin);
    }

    @Test
    void createCourtAsAdminShouldReturn201() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin@test.com", "clave");

        String json = """
                {
                  "nombre":"Pista E2E",
                  "ubicacion":"Interior",
                  "precioHora":30.0,
                  "activa":true
                }
                """;

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/pistaPadel/courts",
                HttpMethod.POST,
                request,
                String.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}