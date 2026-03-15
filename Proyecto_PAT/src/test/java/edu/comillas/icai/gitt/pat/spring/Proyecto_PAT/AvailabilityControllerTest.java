package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void availabilityAsUserShouldReturn200AndJson() throws Exception {
        mockMvc.perform(get("/pistaPadel/availability")
                        .param("date", "2026-02-14")
                        .with(httpBasic("usuario", "clave")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idPista").exists())
                .andExpect(jsonPath("$[0].fecha").exists())
                .andExpect(jsonPath("$[0].franjasDisponibles").isArray());
    }

    @Test
    void availabilityWithoutAuthShouldReturn401() throws Exception {
        mockMvc.perform(get("/pistaPadel/availability")
                        .param("date", "2026-02-14"))
                .andExpect(status().isUnauthorized());
    }
}