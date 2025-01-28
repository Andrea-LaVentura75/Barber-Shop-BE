package it.epicode.Barber.Shop_BE.taglio;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Taglio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String immagineUrl;

    private String descrizione;

    @ManyToOne
    @JoinColumn(name = "barbiere_id", nullable = false)
    private AppUser barbiere;
}

