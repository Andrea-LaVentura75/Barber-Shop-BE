package it.epicode.Barber.Shop_BE.slotDisponibile;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import it.epicode.Barber.Shop_BE.auth.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlotDisponibileService {

    @Autowired
    private SlotDisponibileRepository slotDisponibileRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Transactional
    public void generaSlotDisponibili(Long barbiereId, LocalDateTime inizio, LocalDateTime fine, int rangeMinuti) {
        System.out.println("Generazione slot disponibili");
        System.out.println("Barbiere ID: " + barbiereId);
        System.out.println("Inizio: " + inizio + ", Fine: " + fine);

        while (inizio.isBefore(fine)) {
            SlotDisponibile slot = new SlotDisponibile();
            slot.setBarbiere(appUserRepository.findById(barbiereId)
                    .orElseThrow(() -> new EntityNotFoundException("Barbiere non trovato")));
            slot.setDataOra(inizio);
            slotDisponibileRepository.save(slot);

            inizio = inizio.plusMinutes(rangeMinuti);
        }
    }


    public List<SlotDisponibile> getSlotDisponibili(Long barbiereId) {
        return slotDisponibileRepository.findByBarbiereIdAndPrenotatoFalse(barbiereId);
    }

    public SlotDisponibile aggiornaSlot(Long slotId, boolean prenotato) {
        SlotDisponibile slot = slotDisponibileRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Slot non trovato"));
        slot.setPrenotato(prenotato);
        return slotDisponibileRepository.save(slot);
    }

    public List<SlotDisponibileDTO> trovaSlotDisponibiliPerBarbiere(Long barbiereId, LocalDate giorno) {
        LocalDateTime inizioGiorno = giorno.atStartOfDay();
        LocalDateTime fineGiorno = giorno.atTime(LocalTime.MAX);

        // Recupera gli slot e mappa al DTO
        return slotDisponibileRepository
                .findAllByBarbiereIdAndDataOraBetween(barbiereId, inizioGiorno, fineGiorno)
                .stream()
                .map(slot -> {
                    SlotDisponibileDTO dto = new SlotDisponibileDTO();
                    dto.setDataOra(slot.getDataOra());
                    dto.setPrenotato(slot.isPrenotato());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public void generaSlotRicorrenti(Long barbiereId, LocalTime orarioApertura, LocalTime orarioChiusura, int rangeMinuti, int giorni) {
        AppUser barbiere = appUserRepository.findById(barbiereId)
                .orElseThrow(() -> new EntityNotFoundException("Barbiere non trovato"));

        LocalDate oggi = LocalDate.now();

        for (int i = 0; i < giorni; i++) {
            LocalDateTime inizio = oggi.plusDays(i).atTime(orarioApertura);
            LocalDateTime fine = oggi.plusDays(i).atTime(orarioChiusura);

            while (inizio.isBefore(fine)) {
                SlotDisponibile slot = new SlotDisponibile();
                slot.setBarbiere(barbiere);
                slot.setDataOra(inizio);
                slotDisponibileRepository.save(slot);

                inizio = inizio.plusMinutes(rangeMinuti);
            }
        }
    }

    @Transactional
    public void eliminaVecchiSlot(Long barbiereId, LocalDateTime dataOraCorrente) {
        // Recupera tutti gli slot prima della data/ora corrente
        List<SlotDisponibile> vecchiSlot = slotDisponibileRepository.findAllByBarbiereIdAndDataOraBefore(barbiereId, dataOraCorrente);

        // Elimina gli slot trovati
        slotDisponibileRepository.deleteAll(vecchiSlot);
    }



    public List<SlotDisponibileDTO> trovaSlotDisponibiliPerGiorno(Long barbiereId, LocalDate giorno) {
        return slotDisponibileRepository.findSlotDisponibiliByGiorno(barbiereId, giorno).stream()
                .map(slot -> new SlotDisponibileDTO(slot.getId(), slot.getDataOra(), slot.isPrenotato()))
                .collect(Collectors.toList());
    }





}


