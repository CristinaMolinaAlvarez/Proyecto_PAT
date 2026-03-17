package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.util.Hashing;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    private final UsuarioRepo usuarioRepo;
    private final Hashing hashing;

    public UsersService(UsuarioRepo usuarioRepo, Hashing hashing) {
        this.usuarioRepo = usuarioRepo;
        this.hashing = hashing;
    }

    // Registrar usuario
    public Usuario register(Usuario usuario) {

        // 409 si el email ya existe
        if (usuarioRepo.existsByEmail(usuario.getEmail())) {
            logger.error("No se puede registrar usuario. El email {} ya existe", usuario.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        usuario.setRol(Rol.USER);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);
        usuario.setPassword(hashing.hash(usuario.getPassword()));

        Usuario guardado = usuarioRepo.save(usuario);
        logger.info("Usuario registrado correctamente con email {}", guardado.getEmail());


        return usuarioRepo.save(usuario);
    }

    // Devuelve el usuario autenticado
    public Usuario me(Authentication authentication) {

        // 401 si no está autenticado
        if (authentication == null) {
            logger.error("Acceso a /auth/me sin autenticación");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String email = authentication.getName();

        logger.debug("Usuario autenticado detectado: {}", email);

        return usuarioRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    logger.error("Usuario autenticado {} no encontrado en base de datos", email);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                });
    }

    // GET lista usuarios (ADMIN)
    public Iterable<Usuario> getUsers() {
        return usuarioRepo.findAll();
    }

    // GET usuario por id (ADMIN)
    public Usuario getUser(int userId) {

        return usuarioRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // Modificar usuario
    public Usuario modificarUser(int userId, Usuario usuario) {

        // 404 si el usuario no existe
        Usuario existente = usuarioRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 409 si el email ya existe en otro usuario
        Optional<Usuario> emailExistente = usuarioRepo.findByEmailIgnoreCase(usuario.getEmail());

        if (emailExistente.isPresent() && !emailExistente.get().getIdUsuario().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        // Actualizamos los campos modificables
        existente.setNombre(usuario.getNombre());
        existente.setApellidos(usuario.getApellidos());
        existente.setEmail(usuario.getEmail());
        existente.setTelefono(usuario.getTelefono());
        //No usamos noop, ya que la queremos guardar cifrada
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            existente.setPassword(hashing.hash(usuario.getPassword()));
        }
        // Guardamos el usuario actualizado
        return usuarioRepo.save(existente);
    }
}