package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Pista;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Reserva;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.PistaRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.ReservaRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationsService {

    @Autowired
    private ReservaRepo reservaRepo;
    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private PistaRepo pistaRepo;

    // Crear reserva
    public Reserva crearReserva(Authentication auth, Reserva reserva) {

        Usuario usuario = resolverUsuario(auth);

        // 404 si la pista no existe
        Integer idPista = reserva.getPista().getIdPista();
        Pista pista = pistaRepo.findById(idPista)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 409 si la pista está inactiva
        if (!pista.isActiva()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        // Calcular horaFin
        LocalTime horaFin = reserva.getHoraInicio()
                .plusMinutes(reserva.getDuracionMinutos());

        // 409 si el horario ya está ocupado
        List<Reserva> reservasMismaPistaMismoDia =
                reservaRepo.findByPista_IdPistaAndFechaReserva(idPista, reserva.getFechaReserva());

        boolean ocupado = false;

        for (Reserva r : reservasMismaPistaMismoDia) {
            if (r.getEstado() == Reserva.Estado.ACTIVA) {
                if (r.getHoraInicio().isBefore(horaFin)
                        && reserva.getHoraInicio().isBefore(r.getHoraFin())) {
                    ocupado = true;
                    break;
                }
            }
        }

        if (ocupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        reserva.setUsuario(usuario);
        reserva.setPista(pista);
        reserva.setHoraFin(horaFin);
        reserva.setEstado(Reserva.Estado.ACTIVA);
        reserva.setFechaCreacion(LocalDateTime.now());

        return reservaRepo.save(reserva);
    }

    // Listar reservas
    public List<Reserva> listarReservas(Authentication auth) {

        Usuario usuario = resolverUsuario(auth);

        if (esAdmin(usuario)) {
            List<Reserva> todas = new ArrayList<>();
            reservaRepo.findAll().forEach(todas::add);
            return todas;
        }

        return reservaRepo.findByUsuario_IdUsuario(usuario.getIdUsuario());
    }

    // Obtener una reserva concreta
    public Reserva getReserva(Authentication auth, int id) {

        Usuario usuario = resolverUsuario(auth);

        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 403 si no es suya y no es admin
        if (!esAdmin(usuario) && !reserva.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return reserva;
    }

    // Modificar reserva
    public Reserva reprogramarReserva(Authentication auth, int id, Reserva reservaActualizada) {

        Usuario usuario = resolverUsuario(auth);

        Reserva existente = reservaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!esAdmin(usuario) && !existente.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        LocalTime nuevaHoraFin = reservaActualizada.getHoraInicio()
                .plusMinutes(reservaActualizada.getDuracionMinutos());

        // 409 si el nuevo horario está ocupado
        List<Reserva> reservasMismaPistaMismoDia =
                reservaRepo.findByPista_IdPistaAndFechaReserva(
                        existente.getPista().getIdPista(),
                        reservaActualizada.getFechaReserva()
                );

        boolean ocupado = false;

        for (Reserva r : reservasMismaPistaMismoDia) {
            if (!r.getIdReserva().equals(id) && r.getEstado() == Reserva.Estado.ACTIVA) {
                if (r.getHoraInicio().isBefore(nuevaHoraFin)
                        && reservaActualizada.getHoraInicio().isBefore(r.getHoraFin())) {
                    ocupado = true;
                    break;
                }
            }
        }

        if (ocupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        existente.setFechaReserva(reservaActualizada.getFechaReserva());
        existente.setHoraInicio(reservaActualizada.getHoraInicio());
        existente.setDuracionMinutos(reservaActualizada.getDuracionMinutos());
        existente.setHoraFin(nuevaHoraFin);
        existente.setEstado(Reserva.Estado.ACTIVA);

        return reservaRepo.save(existente);
    }

    // Cancelar reserva
    public void cancelarReserva(Authentication auth, int id) {

        Usuario usuario = resolverUsuario(auth);

        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 403 si no es suya y no es admin
        if (!esAdmin(usuario) && !reserva.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        reserva.setEstado(Reserva.Estado.CANCELADA);
        reservaRepo.save(reserva);
    }

    // Devuelve todas las reservas (uso interno)
    public List<Reserva> getAllInternal() {
        List<Reserva> reservas = new ArrayList<>();
        reservaRepo.findAll().forEach(reservas::add);
        return reservas;
    }

    // Comprueba si es ADMIN
    private boolean esAdmin(Usuario usuario) {
        return usuario.getRol() == Rol.ADMIN;
    }

    // Obtiene el usuario autenticado
    private Usuario resolverUsuario(Authentication auth) {

        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String email = auth.getName();

        Optional<Usuario> usuario = usuarioRepo.findByEmailIgnoreCase(email);

        if (usuario.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return usuario.get();
    }
}