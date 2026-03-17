package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    void getMyReservationsAsUserShouldReturn200AndJsonArray() throws Exception {
        mockMvc.perform(get("/pistaPadel/reservations")
                        .with(httpBasic("usuario@test.com", "clave")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void adminReservationsAsUserShouldReturn403() throws Exception {
        mockMvc.perform(get("/pistaPadel/admin/reservations")
                        .with(httpBasic("usuario@test.com", "clave")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminReservationsAsAdminShouldReturn200AndJsonArray() throws Exception {
        mockMvc.perform(get("/pistaPadel/admin/reservations")
                        .with(httpBasic("admin@test.com", "clave")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray());
    }
}