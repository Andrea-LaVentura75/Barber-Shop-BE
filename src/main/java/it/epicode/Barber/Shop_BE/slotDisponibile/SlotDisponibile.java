package it.epicode.Barber.Shop_BE.slotDisponibile;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "slot_disponibili")
public class SlotDisponibile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barbiere_id", nullable = false)
    private AppUser barbiere;

    @Column(nullable = false)
    private LocalDateTime dataOra;

    @Column(nullable = false)
    private boolean prenotato = false;
}



