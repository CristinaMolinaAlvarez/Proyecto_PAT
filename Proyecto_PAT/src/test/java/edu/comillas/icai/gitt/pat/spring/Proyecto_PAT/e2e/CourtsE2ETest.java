package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.e2e;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CourtsE2ETest {

    @Autowired
    private TestRestTemplate client;

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

        String json = """
                {
                  "nombre":"Pista E2E",
                  "ubicacion":"Interior",
                  "precioHora":30.0,
                  "activa":true
                }
                """;

        TestRestTemplate adminClient = client.withBasicAuth("admin@test.com", "clave");

        ResponseEntity<String> response = adminClient.exchange(
                "/pistaPadel/courts",
                HttpMethod.POST,
                new HttpEntity<>(json, headers),
                String.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}