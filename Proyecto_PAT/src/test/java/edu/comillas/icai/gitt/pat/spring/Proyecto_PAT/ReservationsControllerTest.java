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
class ReservationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMyReservationsAsUserShouldReturn200AndJsonArray() throws Exception {
        mockMvc.perform(get("/pistaPadel/reservations")
                        .with(httpBasic("usuario", "clave")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void adminReservationsAsUserShouldReturn403() throws Exception {
        mockMvc.perform(get("/pistaPadel/admin/reservations")
                        .with(httpBasic("usuario", "clave")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminReservationsAsAdminShouldReturn200AndJsonArray() throws Exception {
        mockMvc.perform(get("/pistaPadel/admin/reservations")
                        .with(httpBasic("admin", "clave")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray());
    }
}