package it.epicode.Barber.Shop_BE.slotDisponibile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SlotDisponibileRepository extends JpaRepository<SlotDisponibile, Long> {
    List<SlotDisponibile> findByBarbiereIdAndPrenotatoFalse(Long barbiereId);
    List<SlotDisponibile> findAllByBarbiereIdAndDataOraBetween(Long barbiereId, LocalDateTime inizio, LocalDateTime fine);
    List<SlotDisponibile> findAllByBarbiereIdAndDataOraBefore(Long barbiereId, LocalDateTime dataOra); // Metodo aggiunto

    @Query("SELECT s FROM SlotDisponibile s WHERE s.barbiere.id = :barbiereId AND s.prenotato = false AND DATE(s.dataOra) = :giorno")
    List<SlotDisponibile> findSlotDisponibiliByGiorno(@Param("barbiereId") Long barbiereId, @Param("giorno") LocalDate giorno);

    Optional<SlotDisponibile> findByBarbiereIdAndDataOra(Long barbiereId, LocalDateTime dataOra);
}



