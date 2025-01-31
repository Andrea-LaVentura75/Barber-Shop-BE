package it.epicode.Barber.Shop_BE.appuntamento;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import it.epicode.Barber.Shop_BE.auth.AppUserService;
import it.epicode.Barber.Shop_BE.auth.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appuntamenti")
@RequiredArgsConstructor
public class AppuntamentoController {

    private final AppuntamentoService appuntamentoService;

    private final AppUserService appUserService;

    // Endpoint per creare un nuovo appuntamento
    @PostMapping
    public ResponseEntity<Appuntamento> creaAppuntamento(@RequestBody Appuntamento appuntamento, Principal principal) {
        Appuntamento nuovoAppuntamento = appuntamentoService.creaAppuntamento(appuntamento, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuovoAppuntamento);
    }



    // Endpoint per ottenere gli appuntamenti di un barbiere
    @GetMapping("/barbiere/{barbiereId}")
    public ResponseEntity<List<AppuntamentoDTO>> trovaAppuntamentiPerBarbiere(
            @PathVariable Long barbiereId, Principal principal) {
        // Verifica che l'utente autenticato corrisponda all'ID del barbiere
        String usernameAutenticato = principal.getName();
        AppUser barbiere = appUserService.findByUsername(usernameAutenticato)
                .orElseThrow(() -> new EntityNotFoundException("Barbiere autenticato non trovato"));

        if (!barbiere.getId().equals(barbiereId)) {
            throw new SecurityException("Non sei autorizzato a visualizzare gli appuntamenti di un altro barbiere.");
        }

        // Recupera gli appuntamenti del barbiere
        List<AppuntamentoDTO> appuntamenti = appuntamentoService.trovaAppuntamentiPerBarbiere(barbiereId);
        return ResponseEntity.ok(appuntamenti);
    }



    // Endpoint per ottenere gli appuntamenti di un cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<AppuntamentoDTO>> trovaAppuntamentiPerCliente(
            @PathVariable Long clienteId, Principal principal) {
        // Verifica che l'utente autenticato corrisponda all'ID cliente
        String usernameAutenticato = principal.getName();
        AppUser cliente = appUserService.findByUsername(usernameAutenticato)
                .orElseThrow(() -> new EntityNotFoundException("Cliente autenticato non trovato"));

        if (!cliente.getId().equals(clienteId)) {
            throw new SecurityException("Non sei autorizzato a visualizzare gli appuntamenti di un altro cliente.");
        }

        List<AppuntamentoDTO> appuntamenti = appuntamentoService.trovaAppuntamentiPerCliente(clienteId);
        return ResponseEntity.ok(appuntamenti);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaAppuntamento(@PathVariable Long id, Principal principal) {
        String usernameAutenticato = principal.getName();
        AppUser utenteAutenticato = appUserService.findByUsername(usernameAutenticato)
                .orElseThrow(() -> new EntityNotFoundException("Utente autenticato non trovato"));

        // Recupera l'appuntamento da eliminare
        Appuntamento appuntamento = appuntamentoService.trovaAppuntamentoPerId(id);

        // Verifica i permessi: cliente o barbiere
        if (utenteAutenticato.getRoles().contains(Role.ROLE_CLIENT)) {
            if (!appuntamento.getCliente().getId().equals(utenteAutenticato.getId())) {
                throw new SecurityException("Non sei autorizzato a eliminare un appuntamento di un altro cliente.");
            }
        } else if (utenteAutenticato.getRoles().contains(Role.ROLE_BARBER)) {
            if (!appuntamento.getBarbiere().getId().equals(utenteAutenticato.getId())) {
                throw new SecurityException("Non sei autorizzato a eliminare un appuntamento di un altro barbiere.");
            }
        }

        // Elimina l'appuntamento
        appuntamentoService.eliminaAppuntamento(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/barbiere/{barbiereId}/giorno/{giorno}")
    public ResponseEntity<List<AppuntamentoDTO>> trovaAppuntamentiPerBarbiereEData(
            @PathVariable Long barbiereId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate giorno,
            Principal principal) {
        // Verifica che l'utente autenticato corrisponda all'ID barbiere
        String usernameAutenticato = principal.getName();
        AppUser barbiere = appUserService.findByUsername(usernameAutenticato)
                .orElseThrow(() -> new EntityNotFoundException("Barbiere autenticato non trovato"));

        if (!barbiere.getId().equals(barbiereId)) {
            throw new SecurityException("Non sei autorizzato a visualizzare gli appuntamenti di un altro barbiere.");
        }

        List<AppuntamentoDTO> appuntamenti = appuntamentoService.trovaAppuntamentiPerBarbiereEData(barbiereId, giorno);
        return ResponseEntity.ok(appuntamenti);
    }

    @PostMapping("/prenota")
    public ResponseEntity<AppuntamentoDTO> prenotaAppuntamento(
            @RequestParam Long slotId,
            @RequestParam Long servizioId,
            @RequestParam(required = false) String nota,
            Principal principal) {

        Appuntamento appuntamento = appuntamentoService.prenotaAppuntamento(slotId, servizioId, nota, principal.getName());
        AppuntamentoDTO appuntamentoDTO = new AppuntamentoDTO(
                appuntamento.getId(),
                appuntamento.getCliente().getNome(),
                appuntamento.getBarbiere().getNome(),
                appuntamento.getDataOra(),
                nota,
                appuntamento.getServizio().getNome(),
                appuntamento.getServizio().getPrezzo()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(appuntamentoDTO);
    }



}

