package it.epicode.Barber.Shop_BE.taglio;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tagli")
@RequiredArgsConstructor
public class TaglioController {

    private final TaglioService taglioService;

    @PostMapping("/{barbiereId}")
    public ResponseEntity<Taglio> aggiungiTaglio(
            @PathVariable Long barbiereId,
            @RequestParam("immagine") MultipartFile immagine,
            @RequestParam(value = "descrizione", required = false) String descrizione) {
        Taglio nuovoTaglio = taglioService.aggiungiTaglio(barbiereId, immagine, descrizione);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuovoTaglio);
    }

    @GetMapping("/{barbiereId}")
    public ResponseEntity<List<Taglio>> trovaTagliPerBarbiere(@PathVariable Long barbiereId) {
        return ResponseEntity.ok(taglioService.trovaTagliPerBarbiere(barbiereId));
    }

    @DeleteMapping("/{taglioId}")
    public ResponseEntity<Void> eliminaTaglio(@PathVariable Long taglioId, Principal principal) {
        taglioService.eliminaTaglio(taglioId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}

