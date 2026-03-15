package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class ProyectoPatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoPatApplication.class, args);
	}

	@Bean
	CommandLineRunner crearAdmin(UsuarioRepo usuarioRepo) {
		return args -> {
			// Comprueba si ya existe un usuario con email admin@padel.com
			// Si no existe, se crea automáticamente al arrancar la aplicación

			if (usuarioRepo.findByEmailIgnoreCase("admin@padel.com").isEmpty()) {

				Usuario admin = new Usuario();
				admin.setNombre("Admin");
				admin.setApellidos("Sistema");
				admin.setEmail("admin@padel.com");
				admin.setPassword("admin");
				admin.setRol(Rol.ADMIN);
				admin.setActivo(true);
				admin.setFechaRegistro(LocalDateTime.now());

				usuarioRepo.save(admin);
			}
		};
	}
}
