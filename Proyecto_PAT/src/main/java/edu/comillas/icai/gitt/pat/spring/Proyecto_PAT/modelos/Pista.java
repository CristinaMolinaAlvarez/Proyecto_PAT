package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Pista {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer idPista;

        @NotBlank(message = "El nombre no puede estar vacío")
        @Column(nullable = false)
        private String nombre;

        @NotBlank(message = "La ubicación no puede estar vacía")
        @Column(nullable = false)
        private String ubicacion;

        @Positive(message = "El precio debe ser mayor que 0")
        @Column(nullable = false)
        private double precioHora;

        @Column(nullable = false)
        private boolean activa;

        private LocalDateTime fechaAlta;

        // Una pista puede tener muchas reservas
        // Si se borra una pista se borran sus reservas
        @OneToMany(mappedBy = "pista")
        @OnDelete(action = OnDeleteAction.CASCADE)
        private List<Reserva> reservas;

        public Pista() {}

        public Pista(Integer idPista, String nombre, String ubicacion, double precioHora, boolean activa, LocalDateTime fechaAlta) {
                this.idPista = idPista;
                this.nombre = nombre;
                this.ubicacion = ubicacion;
                this.precioHora = precioHora;
                this.activa = activa;
                this.fechaAlta = fechaAlta;
        }

        public Integer getIdPista() {
                return idPista;
        }

        public void setIdPista(Integer idPista) {
                this.idPista = idPista;
        }

        public String getNombre() {
                return nombre;
        }

        public String getUbicacion() {
                return ubicacion;
        }

        public double getPrecioHora() {
                return precioHora;
        }

        public boolean isActiva() {
                return activa;
        }

        public LocalDateTime getFechaAlta() {
                return fechaAlta;
        }

}