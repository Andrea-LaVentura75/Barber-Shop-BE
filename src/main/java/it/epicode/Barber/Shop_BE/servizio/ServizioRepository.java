package it.epicode.Barber.Shop_BE.servizio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServizioRepository extends JpaRepository<Servizio, Long> {
    List<Servizio> findByBarbiereId(Long barbiereId);
}

