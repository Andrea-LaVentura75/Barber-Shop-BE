package it.epicode.Barber.Shop_BE.appuntamento;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import it.epicode.Barber.Shop_BE.servizio.Servizio;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appuntamenti")
public class Appuntamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private AppUser cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barbiere_id", nullable = false)
    private AppUser barbiere;

    @Column(nullable = false)
    private LocalDateTime dataOra;

    private String nota;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "servizio_id", nullable = false)
    private Servizio servizio;


}