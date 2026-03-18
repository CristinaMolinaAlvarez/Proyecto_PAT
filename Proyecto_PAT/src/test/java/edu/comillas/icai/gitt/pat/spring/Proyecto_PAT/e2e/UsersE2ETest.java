package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.e2e;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UsersE2ETest {

    @Autowired
    private TestRestTemplate client;

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

        ResponseEntity<String> response = client.exchange(
                "/pistaPadel/auth/register",
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

        ResponseEntity<String> first = client.exchange(
                "/pistaPadel/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(json, headers),
                String.class
        );

        ResponseEntity<String> second = client.exchange(
                "/pistaPadel/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(json, headers),
                String.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, first.getStatusCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, second.getStatusCode());
    }
}