package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.services;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Reserva;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.ReservaRepo;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TareasProgramadas {

    private static final Logger log = LoggerFactory.getLogger(TareasProgramadas.class);

    private final ReservaRepo reservaRepo;
    private final UsuarioRepo usuarioRepo;

    public TareasProgramadas(ReservaRepo reservaRepo, UsuarioRepo usuarioRepo) {
        this.reservaRepo = reservaRepo;
        this.usuarioRepo = usuarioRepo;
    }


    @Scheduled(cron = "0 0 2 * * *") //segundo 0, minuto 0, hora 2 , todos los días, todos los meses, cualquier día de la semana
    public void enviarRecordatorioReservas() {
        log.info("Ejecutando tarea programada: recordatorio diario de reservas");

        LocalDate hoy = LocalDate.now();

        List<Reserva> reservasDeHoy = reservaRepo.findByFechaReserva(hoy);

        for (Reserva reserva : reservasDeHoy) {
            if (reserva.getUsuario() != null && reserva.getEstado() == Reserva.Estado.ACTIVA) {
                log.info("Recordatorio enviado a {} para su reserva de hoy en pista {} a las {}",
                        reserva.getUsuario().getEmail(),
                        reserva.getPista().getNombre(),
                        reserva.getHoraInicio());
            }
        }
    }

    @Scheduled(cron = "0 0 2 1 * *")  //segundo 0 minuto 0 hora 2, dia del mes 1....
    public void enviarResumenMensualDisponibilidad() {
        log.info("Ejecutando tarea programada: resumen mensual de disponibilidad");

        List<Usuario> usuarios = (List<Usuario>) usuarioRepo.findAll();

        for (Usuario usuario : usuarios) {
            log.info("Resumen mensual enviado a {}", usuario.getEmail());
        }
    }
}