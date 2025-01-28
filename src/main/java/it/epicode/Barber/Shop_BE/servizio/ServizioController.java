package it.epicode.Barber.Shop_BE.servizio;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/servizi")
@RequiredArgsConstructor
public class ServizioController {

    private final ServizioService servizioService;

    // Aggiungi un nuovo servizio per un barbiere
    @PostMapping("/{barbiereId}")
    public ResponseEntity<Servizio> aggiungiServizio(@PathVariable Long barbiereId, @RequestBody Servizio servizio, Principal principal) {
        // Verifica che l'utente autenticato sia il barbiere
        if (!principal.getName().equals(servizioService.getBarbiereUsername(barbiereId))) {
            throw new SecurityException("Non sei autorizzato ad aggiungere servizi per questo barbiere.");
        }

        Servizio nuovoServizio = servizioService.aggiungiServizio(barbiereId, servizio);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuovoServizio);
    }


    // Recupera tutti i servizi di un barbiere
    @GetMapping("/{barbiereId}")
    public ResponseEntity<List<Servizio>> trovaServiziPerBarbiere(@PathVariable Long barbiereId) {
        List<Servizio> servizi = servizioService.trovaServiziPerBarbiere(barbiereId);
        return ResponseEntity.ok(servizi);
    }

    @PutMapping("/{servizioId}")
    public ResponseEntity<Servizio> modificaServizio(
            @PathVariable Long servizioId,
            @RequestBody Servizio servizio,
            Principal principal) {
        Servizio servizioAggiornato = servizioService.modificaServizio(servizioId, servizio, principal.getName());
        return ResponseEntity.ok(servizioAggiornato);
    }

    @DeleteMapping("/{servizioId}")
    public ResponseEntity<Void> eliminaServizio(@PathVariable Long servizioId, Principal principal) {
        servizioService.eliminaServizio(servizioId, principal.getName());
        return ResponseEntity.noContent().build();
    }


}

