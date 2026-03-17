package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Pista;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Reserva;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepo extends CrudRepository<Reserva, Integer> {
    // reservas de un usuario
    List<Reserva> findByUsuario(Usuario usuario);

    // reservas de una pista
    List<Reserva> findByPista(Pista pista);

    // reservas de una pista en un día (availability)
    List<Reserva> findByPistaAndFechaReserva(Pista pista, LocalDate fechaReserva);

    // reservas de un usuario en un día
    List<Reserva> findByUsuarioAndFechaReserva(Usuario usuario, LocalDate fechaReserva);

    // reservas por idUsuario (útil para filtros)
    List<Reserva> findByUsuario_IdUsuario(Integer idUsuario);

    // reservas por idPista
    List<Reserva> findByPista_IdPista(Integer idPista);

    // reservas por pista y fecha
    List<Reserva> findByPista_IdPistaAndFechaReserva(Integer idPista, LocalDate fecha);

    List<Reserva> findByFechaReserva(LocalDate fechaReserva);
    // métodos básicos
    Iterable<Reserva> findAll();
    Optional<Reserva> findById(Integer id);
    Reserva save(Reserva reserva);
    void deleteById(Integer id);
    boolean existsById(Integer id);

}