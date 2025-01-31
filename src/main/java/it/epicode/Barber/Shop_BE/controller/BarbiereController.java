package it.epicode.Barber.Shop_BE.controller;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import it.epicode.Barber.Shop_BE.auth.AppUserRepository;
import it.epicode.Barber.Shop_BE.slotDisponibile.SlotDisponibile;
import it.epicode.Barber.Shop_BE.slotDisponibile.SlotDisponibileDTO;
import it.epicode.Barber.Shop_BE.slotDisponibile.SlotDisponibileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/barbiere")
@RequiredArgsConstructor
public class BarbiereController {

    private final SlotDisponibileService slotDisponibileService;

    private final AppUserRepository appUserRepository;

    // Endpoint per creare slot disponibili
    @PostMapping("/slot")
    public ResponseEntity<String> creaSlotDisponibili(
            @RequestParam Long barbiereId,
            @RequestParam String data, // "yyyy-MM-dd"
            @RequestParam String orarioApertura, // "HH:mm"
            @RequestParam String orarioChiusura, // "HH:mm"
            @RequestParam int rangeMinuti) {

        try {
            // Converte i parametri stringa in LocalDate e LocalTime
            LocalDate giorno = LocalDate.parse(data); // Data del giorno
            LocalTime apertura = LocalTime.parse(orarioApertura);
            LocalTime chiusura = LocalTime.parse(orarioChiusura);

            // Combina data e orario in LocalDateTime
            LocalDateTime inizio = giorno.atTime(apertura);
            LocalDateTime fine = giorno.atTime(chiusura);

            // Chiama il service
            slotDisponibileService.generaSlotDisponibili(barbiereId, inizio, fine, rangeMinuti);
            return ResponseEntity.ok("Slot creati con successo");

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato data/ora non valido. Usa 'yyyy-MM-dd' per la data e 'HH:mm' per l'orario.");
        }
    }



    // Endpoint per ottenere tutti gli slot disponibili di un barbiere
    @GetMapping("/slot/{barbiereId}")
    public ResponseEntity<List<SlotDisponibile>> getSlotDisponibili(@PathVariable Long barbiereId) {
        List<SlotDisponibile> slotDisponibili = slotDisponibileService.getSlotDisponibili(barbiereId);
        return ResponseEntity.ok(slotDisponibili);
    }

    // Endpoint per aggiornare lo stato di uno slot (prenotato o meno)
    @PutMapping("/slot/{slotId}")
    public ResponseEntity<SlotDisponibile> aggiornaSlot(@PathVariable Long slotId, @RequestParam boolean prenotato) {
        SlotDisponibile slotAggiornato = slotDisponibileService.aggiornaSlot(slotId, prenotato);
        return ResponseEntity.ok(slotAggiornato);
    }

    @GetMapping("/{barbiereId}/slot")
    public ResponseEntity<List<SlotDisponibileDTO>> trovaSlotDisponibili(
            @PathVariable Long barbiereId,
            @RequestParam String data) {

        try {
            LocalDate giorno = LocalDate.parse(data);
            List<SlotDisponibileDTO> slotDisponibili = slotDisponibileService.trovaSlotDisponibiliPerBarbiere(barbiereId, giorno);
            return ResponseEntity.ok(slotDisponibili);

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build(); // Risposta per formato data errato
        }
    }

    @PostMapping("/slot-ricorrenti")
    public ResponseEntity<Map<String, String>> generaSlotRicorrenti(
            @RequestParam Long barbiereId,
            @RequestParam String orarioApertura,
            @RequestParam String orarioChiusura,
            @RequestParam int rangeMinuti,
            @RequestParam int giorni) {

        LocalTime apertura = LocalTime.parse(orarioApertura);
        LocalTime chiusura = LocalTime.parse(orarioChiusura);

        // Genera gli slot ricorrenti
        slotDisponibileService.generaSlotRicorrenti(barbiereId, apertura, chiusura, rangeMinuti, giorni);

        // Crea una risposta JSON
        Map<String, String> response = new HashMap<>();
        response.put("message", "Slot ricorrenti creati con successo");

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/slot/vecchi")
    public ResponseEntity<String> eliminaVecchiSlot(@RequestParam Long barbiereId) {
        LocalDateTime dataOraCorrente = LocalDateTime.now();
        slotDisponibileService.eliminaVecchiSlot(barbiereId, dataOraCorrente);
        return ResponseEntity.ok("Vecchi slot eliminati con successo");
    }

    @GetMapping("/cerca")
    public ResponseEntity<List<BarbiereDTO>> cercaBarbieri(@RequestParam String nomeSalone) {
        // Log iniziale per la chiamata
        System.out.println("Metodo cercaBarbieri chiamato.");
        System.out.println("Parametro nomeSalone ricevuto: " + nomeSalone);

        // Trova i barbieri il cui salone contiene il nome cercato (ignorando maiuscole/minuscole)
        List<AppUser> barbieri = appUserRepository.findByNomeSaloneContainingIgnoreCase(nomeSalone);
        System.out.println("Barbieri trovati nel repository: " + barbieri.size());

        // Filtra solo i barbieri e converte in DTO
        List<BarbiereDTO> risultati = barbieri.stream()
                .filter(AppUser::isBarber) // Assicurati che siano barbieri
                .map(barbiere -> {
                    System.out.println("Barbiere trovato: " + barbiere.getNome() + " " + barbiere.getCognome());
                    return new BarbiereDTO(
                            barbiere.getId(),
                            barbiere.getNome(),
                            barbiere.getCognome(),
                            barbiere.getNomeSalone(),
                            barbiere.getAvatar()
                    );
                })
                .collect(Collectors.toList());

        System.out.println("Numero di risultati finali: " + risultati.size());
        return ResponseEntity.ok(risultati);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BarbiereDTO> getBarbiere(@PathVariable Long id) {
        System.out.println("Richiesta ricevuta per barbiere ID: " + id);
        AppUser barbiere = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Barbiere non trovato"));
        if (!barbiere.isBarber()) {
            throw new SecurityException("Non è un barbiere");
        }

        BarbiereDTO dto = new BarbiereDTO(barbiere.getId(), barbiere.getNome(), barbiere.getCognome(),
                barbiere.getNomeSalone(), barbiere.getAvatar());
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/{barbiereId}/slot-disponibili")
    public ResponseEntity<List<SlotDisponibileDTO>> trovaSlotDisponibiliPerGiorno(
            @PathVariable Long barbiereId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate giorno) {

        List<SlotDisponibileDTO> slotDisponibili = slotDisponibileService.trovaSlotDisponibiliPerGiorno(barbiereId, giorno);
        return ResponseEntity.ok(slotDisponibili);
    }



}


