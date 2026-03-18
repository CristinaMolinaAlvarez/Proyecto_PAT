package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.integration;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourtsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararUsuariosDePrueba() {
        usuarioRepo.findByEmailIgnoreCase("usuario@test.com").ifPresent(usuarioRepo::delete);
        usuarioRepo.findByEmailIgnoreCase("admin@test.com").ifPresent(usuarioRepo::delete);

        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario");
        usuario.setApellidos("Prueba");
        usuario.setEmail("usuario@test.com");
        usuario.setPassword(passwordEncoder.encode("clave"));
        usuario.setRol(Rol.USER);
        usuarioRepo.save(usuario);

        Usuario admin = new Usuario();
        admin.setNombre("Admin");
        admin.setApellidos("Prueba");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("clave"));
        admin.setRol(Rol.ADMIN);
        usuarioRepo.save(admin);
    }

    @Test
    void getCourtsAsUserShouldReturn200AndJsonArray() throws Exception {
        mockMvc.perform(get("/pistaPadel/courts")
                        .with(httpBasic("usuario@test.com", "clave")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createCourtAsUserShouldReturn403() throws Exception {
        String body = """
                {
                  "nombre":"Pista X",
                  "ubicacion":"Interior",
                  "precioHora":30.0,
                  "activa":true
                }
                """;

        mockMvc.perform(post("/pistaPadel/courts")
                        .with(httpBasic("usuario@test.com", "clave"))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCourtAsAdminShouldReturn201() throws Exception {
        String body = """
                {
                  "nombre":"Pista X",
                  "ubicacion":"Interior",
                  "precioHora":30.0,
                  "activa":true
                }
                """;

        mockMvc.perform(post("/pistaPadel/courts")
                        .with(httpBasic("admin@test.com", "clave"))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void getCourtByIdShouldReturn200() throws Exception {
        mockMvc.perform(get("/pistaPadel/courts/1")
                        .with(httpBasic("usuario@test.com", "clave")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.idPista").exists());
    }

    @Test
    void updateCourtAsAdminShouldReturn200() throws Exception {
        String body = """
                {
                  "nombre":"Pista Modificada",
                  "ubicacion":"Exterior",
                  "precioHora":35.0,
                  "activa":true
                }
                """;

        mockMvc.perform(patch("/pistaPadel/courts/1")
                        .with(httpBasic("admin@test.com", "clave"))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCourtAsAdminShouldReturn204() throws Exception {
        mockMvc.perform(delete("/pistaPadel/courts/1")
                        .with(httpBasic("admin@test.com", "clave")))
                .andExpect(status().isNoContent());
    }
}