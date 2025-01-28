package it.epicode.Barber.Shop_BE.prodotto;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Prodotto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descrizione;

    @Column(nullable = false)
    private Double prezzo;

    private String immagineUrl;

    @ManyToOne
    @JoinColumn(name = "barbiere_id", nullable = false)
    private AppUser barbiere;
}

