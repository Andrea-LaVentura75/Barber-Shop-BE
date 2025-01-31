package it.epicode.Barber.Shop_BE.appuntamento;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import it.epicode.Barber.Shop_BE.auth.AppUserRepository;
import it.epicode.Barber.Shop_BE.servizio.Servizio;
import it.epicode.Barber.Shop_BE.servizio.ServizioRepository;
import it.epicode.Barber.Shop_BE.slotDisponibile.SlotDisponibile;
import it.epicode.Barber.Shop_BE.slotDisponibile.SlotDisponibileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppuntamentoService {

    private final AppuntamentoRepository appuntamentoRepository;

    private final AppUserRepository appUserRepository;

    private final SlotDisponibileRepository slotDisponibileRepository;

    private final ServizioRepository servizioRepository;

    // Salva un nuovo appuntamento dopo aver verificato che non ci siano sovrapposizioni
    public Appuntamento creaAppuntamento(Appuntamento appuntamento, String usernameAutenticato) {
        // Carica il cliente e il barbiere dal database
        AppUser cliente = appUserRepository.findById(appuntamento.getCliente().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente non trovato con ID: " + appuntamento.getCliente().getId()));
        AppUser barbiere = appUserRepository.findById(appuntamento.getBarbiere().getId())
                .orElseThrow(() -> new EntityNotFoundException("Barbiere non trovato con ID: " + appuntamento.getBarbiere().getId()));

        // Verifica che l'utente autenticato corrisponda al cliente
        if (!cliente.getUsername().equals(usernameAutenticato)) {
            throw new SecurityException("Non sei autorizzato a creare un appuntamento per un altro cliente.");
        }

        // Associa gli oggetti completi al nuovo appuntamento
        appuntamento.setCliente(cliente);
        appuntamento.setBarbiere(barbiere);

        // Verifica che non ci siano sovrapposizioni di appuntamenti
        boolean esisteSovrapposizione = appuntamentoRepository.existsByBarbiereAndDataOra(
                barbiere.getId(),
                appuntamento.getDataOra()
        );

        if (esisteSovrapposizione) {
            throw new IllegalArgumentException("Il barbiere ha già un appuntamento in questa data e ora.");
        }

        // Aggiorna lo stato dello slot come prenotato
        SlotDisponibile slot = slotDisponibileRepository.findByBarbiereIdAndDataOra(
                barbiere.getId(), appuntamento.getDataOra()
        ).orElseThrow(() -> new EntityNotFoundException("Slot non trovato per la data e ora selezionata."));

        slot.setPrenotato(true);
        slotDisponibileRepository.save(slot);

        // Salva l'appuntamento
        return appuntamentoRepository.save(appuntamento);
    }



    // Ottieni tutti gli appuntamenti di un barbiere
    public List<AppuntamentoDTO> trovaAppuntamentiPerBarbiere(Long barbiereId) {
        return appuntamentoRepository.findAll().stream()
                .filter(app -> app.getBarbiere().getId().equals(barbiereId))
                .map(app -> {
                    AppuntamentoDTO dto = new AppuntamentoDTO();
                    dto.setId(app.getId());
                    dto.setClienteNome(app.getCliente().getNome());
                    dto.setBarbiereNome(app.getBarbiere().getNome());
                    dto.setDataOra(app.getDataOra());
                    dto.setNota(app.getNota());

                    // Aggiungi i dettagli del servizio
                    if (app.getServizio() != null) {
                        dto.setServizioNome(app.getServizio().getNome());
                        dto.setServizioPrezzo(app.getServizio().getPrezzo());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }



    // Ottieni tutti gli appuntamenti di un cliente
    public List<AppuntamentoDTO> trovaAppuntamentiPerCliente(Long clienteId) {
        return appuntamentoRepository.findByClienteId(clienteId).stream()
                .map(app -> {
                    AppuntamentoDTO dto = new AppuntamentoDTO();
                    dto.setId(app.getId());
                    dto.setClienteNome(app.getCliente().getNome());
                    dto.setBarbiereNome(app.getBarbiere().getNome());
                    dto.setDataOra(app.getDataOra());
                    dto.setNota(app.getNota());

                    // Aggiungi le informazioni del servizio
                    if (app.getServizio() != null) {
                        dto.setServizioNome(app.getServizio().getNome());
                        dto.setServizioPrezzo(app.getServizio().getPrezzo());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }



    public void eliminaAppuntamento(Long id) {
        Appuntamento appuntamento = appuntamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appuntamento non trovato con ID: " + id));
        appuntamentoRepository.delete(appuntamento);
    }

    public List<AppuntamentoDTO> trovaAppuntamentiPerBarbiereEData(Long barbiereId, LocalDate giorno) {
        return appuntamentoRepository.findByBarbiereAndGiorno(barbiereId, giorno).stream()
                .map(app -> {
                    AppuntamentoDTO dto = new AppuntamentoDTO();
                    dto.setId(app.getId());
                    dto.setClienteNome(app.getCliente().getNome());
                    dto.setBarbiereNome(app.getBarbiere().getNome());
                    dto.setDataOra(app.getDataOra());
                    dto.setNota(app.getNota());

                    // Associa i dettagli del servizio
                    if (app.getServizio() != null) {
                        dto.setServizioNome(app.getServizio().getNome());
                        dto.setServizioPrezzo(app.getServizio().getPrezzo());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


    public Appuntamento trovaAppuntamentoPerId(Long id) {
        return appuntamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appuntamento non trovato con ID: " + id));
    }

    @Transactional
    public Appuntamento prenotaAppuntamento(Long slotId, Long servizioId, String nota, String usernameCliente) {
        // Recupera il cliente autenticato
        AppUser cliente = appUserRepository.findByUsername(usernameCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente non trovato"));

        // Recupera lo slot e verifica che sia disponibile
        SlotDisponibile slot = slotDisponibileRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Slot non trovato"));
        if (slot.isPrenotato()) {
            throw new IllegalArgumentException("Questo slot è già prenotato.");
        }

        // Recupera il servizio scelto
        Servizio servizio = servizioRepository.findById(servizioId)
                .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato"));

        // Imposta lo slot come prenotato
        slot.setPrenotato(true);
        slotDisponibileRepository.save(slot);

        // Crea il nuovo appuntamento
        Appuntamento appuntamento = new Appuntamento();
        appuntamento.setCliente(cliente);
        appuntamento.setBarbiere(slot.getBarbiere());
        appuntamento.setDataOra(slot.getDataOra());
        appuntamento.setNota(nota);
        appuntamento.setServizio(servizio);

        return appuntamentoRepository.save(appuntamento);
    }


}

