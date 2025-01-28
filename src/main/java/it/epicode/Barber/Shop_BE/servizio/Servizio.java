package it.epicode.Barber.Shop_BE.servizio;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Servizio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Double prezzo;

    @ManyToOne
    @JoinColumn(name = "barbiere_id", nullable = false)
    private AppUser barbiere;
}
