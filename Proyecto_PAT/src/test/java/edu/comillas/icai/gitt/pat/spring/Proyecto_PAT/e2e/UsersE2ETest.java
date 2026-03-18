package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.e2e;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UsersE2ETest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void registerUserShouldReturn201() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = """
                {
                  "nombre":"Nuevo",
                  "apellidos":"Usuario",
                  "email":"nuevoe2e@test.com",
                  "password":"clave123"
                }
                """;

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/pistaPadel/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(json, headers),
                String.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void duplicatedRegisterShouldReturnConflict() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = """
                {
                  "nombre":"Duplicado",
                  "apellidos":"Usuario",
                  "email":"duplicado@test.com",
                  "password":"clave123"
                }
                """;

        ResponseEntity<String> first = restTemplate.exchange(
                "http://localhost:" + port + "/pistaPadel/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(json, headers),
                String.class
        );

        ResponseEntity<String> second = restTemplate.exchange(
                "http://localhost:" + port + "/pistaPadel/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(json, headers),
                String.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, first.getStatusCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, second.getStatusCode());
    }
}