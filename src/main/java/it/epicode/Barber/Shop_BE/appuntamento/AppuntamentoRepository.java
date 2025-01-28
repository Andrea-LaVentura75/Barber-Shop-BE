package it.epicode.Barber.Shop_BE.appuntamento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppuntamentoRepository extends JpaRepository<Appuntamento, Long> {

    @Query("SELECT COUNT(a) > 0 FROM Appuntamento a WHERE a.barbiere.id = :barbiereId AND a.dataOra = :dataOra")
    boolean existsByBarbiereAndDataOra(@Param("barbiereId") Long barbiereId, @Param("dataOra") LocalDateTime dataOra);

    @Query("SELECT a FROM Appuntamento a JOIN FETCH a.servizio WHERE a.barbiere.id = :barbiereId AND DATE(a.dataOra) = :giorno")
    List<Appuntamento> findByBarbiereAndGiorno(@Param("barbiereId") Long barbiereId, @Param("giorno") LocalDate giorno);

    List<Appuntamento> findByClienteId(Long clienteId);

    List<Appuntamento> findByBarbiereId(Long barbiereId);
}

