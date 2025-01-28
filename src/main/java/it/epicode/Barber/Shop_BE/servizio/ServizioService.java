package it.epicode.Barber.Shop_BE.servizio;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import it.epicode.Barber.Shop_BE.auth.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServizioService {

    private final ServizioRepository servizioRepository;
    private final AppUserRepository appUserRepository;

    public Servizio aggiungiServizio(Long barbiereId, Servizio servizio) {
        AppUser barbiere = appUserRepository.findById(barbiereId)
                .orElseThrow(() -> new EntityNotFoundException("Barbiere non trovato con ID: " + barbiereId));

        if (!barbiere.isBarber()) {
            throw new IllegalArgumentException("Solo i barbieri possono aggiungere servizi.");
        }

        servizio.setBarbiere(barbiere);
        return servizioRepository.save(servizio);
    }

    public List<Servizio> trovaServiziPerBarbiere(Long barbiereId) {
        return servizioRepository.findByBarbiereId(barbiereId);
    }

    public String getBarbiereUsername(Long barbiereId) {
        AppUser barbiere = appUserRepository.findById(barbiereId)
                .orElseThrow(() -> new EntityNotFoundException("Barbiere non trovato con ID: " + barbiereId));

        if (!barbiere.isBarber()) {
            throw new IllegalArgumentException("L'utente con ID " + barbiereId + " non è un barbiere.");
        }

        return barbiere.getUsername();
    }

    public Servizio modificaServizio(Long servizioId, Servizio servizio, String usernameBarbiere) {
        Servizio servizioEsistente = servizioRepository.findById(servizioId)
                .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato con ID: " + servizioId));

        if (!servizioEsistente.getBarbiere().getUsername().equals(usernameBarbiere)) {
            throw new SecurityException("Non sei autorizzato a modificare questo servizio.");
        }

        servizioEsistente.setNome(servizio.getNome());
        servizioEsistente.setPrezzo(servizio.getPrezzo());

        return servizioRepository.save(servizioEsistente);
    }

    public void eliminaServizio(Long servizioId, String usernameBarbiere) {
        Servizio servizio = servizioRepository.findById(servizioId)
                .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato con ID: " + servizioId));

        if (!servizio.getBarbiere().getUsername().equals(usernameBarbiere)) {
            throw new SecurityException("Non sei autorizzato a eliminare questo servizio.");
        }

        servizioRepository.delete(servizio);
    }



}

