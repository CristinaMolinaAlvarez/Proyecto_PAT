package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourtsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCourtsAsUserShouldReturn200AndJsonArray() throws Exception {
        mockMvc.perform(get("/pistaPadel/courts")
                        .with(httpBasic("usuario", "clave")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray())
                // En los datos iniciales suelen venir 3 pistas
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].idPista").exists())
                .andExpect(jsonPath("$[0].nombre").exists());
    }

    @Test
    void createCourtAsUserShouldReturn403() throws Exception {
        String body = """
                {
                  \"nombre\":\"Pista X\",
                  \"ubicacion\":\"Interior\",
                  \"precioHora\":30.0,
                  \"activa\":true
                }
                """;

        mockMvc.perform(post("/pistaPadel/courts")
                        .with(httpBasic("usuario", "clave"))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCourtAsAdminShouldReturn201() throws Exception {
        String body = """
                {
                  \"nombre\":\"Pista X\",
                  \"ubicacion\":\"Interior\",
                  \"precioHora\":30.0,
                  \"activa\":true
                }
                """;

        mockMvc.perform(post("/pistaPadel/courts")
                        .with(httpBasic("admin", "clave"))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated());
    }
}
