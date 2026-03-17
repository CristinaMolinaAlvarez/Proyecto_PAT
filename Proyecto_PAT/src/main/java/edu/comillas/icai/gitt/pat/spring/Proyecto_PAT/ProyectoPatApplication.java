package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.util.Hashing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;


@SpringBootApplication
public class ProyectoPatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoPatApplication.class, args);
	}

	@Bean
	CommandLineRunner crearAdmin(UsuarioRepo usuarioRepo, Hashing hashing) {
		return args -> {
			// Comprueba si ya existe un usuario con email admin@padel.com
			// Si no existe, se crea automáticamente al arrancar la aplicación

			if (usuarioRepo.findByEmailIgnoreCase("admin@padel.com").isEmpty()) {

				Usuario admin = new Usuario();
				admin.setNombre("Admin");
				admin.setApellidos("Sistema");
				admin.setEmail("admin@padel.com");

				//Ciframos la contraseña
				admin.setPassword(hashing.hash("admin"));
				admin.setRol(Rol.ADMIN);
				admin.setActivo(true);
				admin.setFechaRegistro(LocalDateTime.now());

				usuarioRepo.save(admin);
			}
		};
	}
}
