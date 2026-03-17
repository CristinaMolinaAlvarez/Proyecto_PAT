package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararUsuarioDePrueba() {
        usuarioRepo.findByEmailIgnoreCase("usuario@test.com").ifPresent(usuarioRepo::delete);

        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario");
        usuario.setApellidos("Prueba");
        usuario.setEmail("usuario@test.com");
        usuario.setPassword(passwordEncoder.encode("clave"));
        usuario.setRol(Rol.USER);

        usuarioRepo.save(usuario);
    }

    @Test
    void availabilityAsUserShouldReturn200AndJson() throws Exception {
        mockMvc.perform(get("/pistaPadel/availability")
                        .param("date", "2026-02-14")
                        .with(httpBasic("usuario@test.com", "clave")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void availabilityWithoutAuthShouldReturn401() throws Exception {
        mockMvc.perform(get("/pistaPadel/availability")
                        .param("date", "2026-02-14"))
                .andExpect(status().isUnauthorized());
    }

    //fecha obligatoria o inválida
    @Test
    void availabilityWithoutDateShouldReturn400() throws Exception {
        mockMvc.perform(get("/pistaPadel/availability")
                        .with(httpBasic("usuario", "clave")))
                .andExpect(status().isBadRequest());
    }

}