package it.epicode.Barber.Shop_BE.slotDisponibile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SlotDisponibileRepository extends JpaRepository<SlotDisponibile, Long> {
    List<SlotDisponibile> findByBarbiereIdAndPrenotatoFalse(Long barbiereId);
    List<SlotDisponibile> findAllByBarbiereIdAndDataOraBetween(Long barbiereId, LocalDateTime inizio, LocalDateTime fine);
    List<SlotDisponibile> findAllByBarbiereIdAndDataOraBefore(Long barbiereId, LocalDateTime dataOra); // Metodo aggiunto
}



