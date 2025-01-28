package it.epicode.Barber.Shop_BE.taglio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaglioRepository extends JpaRepository<Taglio, Long> {
    List<Taglio> findByBarbiereId(Long barbiereId);
}

