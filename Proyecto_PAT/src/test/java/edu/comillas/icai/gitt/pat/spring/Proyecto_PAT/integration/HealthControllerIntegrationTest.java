package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthShouldReturnOk() throws Exception {
        mockMvc.perform(get("/pistaPadel/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }
}