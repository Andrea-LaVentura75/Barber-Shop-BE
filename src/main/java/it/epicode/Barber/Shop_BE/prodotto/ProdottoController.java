package it.epicode.Barber.Shop_BE.prodotto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/prodotti")
@RequiredArgsConstructor
public class ProdottoController {

    private final ProdottoService prodottoService;

    @PostMapping("/{barbiereId}")
    public ResponseEntity<Prodotto> aggiungiProdotto(
            @PathVariable Long barbiereId,
            @RequestParam("prodotto") String prodottoJson,
            @RequestParam(value = "immagine", required = false) MultipartFile immagine) {
        Prodotto prodotto;
        try {
            prodotto = new ObjectMapper().readValue(prodottoJson, Prodotto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Errore durante la deserializzazione del prodotto JSON.", e);
        }

        Prodotto nuovoProdotto = prodottoService.aggiungiProdotto(barbiereId, prodotto, immagine);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuovoProdotto);
    }




    @GetMapping("/{barbiereId}")
    public ResponseEntity<List<Prodotto>> trovaProdottiPerBarbiere(@PathVariable Long barbiereId) {
        return ResponseEntity.ok(prodottoService.trovaProdottiPerBarbiere(barbiereId));
    }

    @PutMapping("/{prodottoId}")
    public ResponseEntity<Prodotto> modificaProdotto(
            @PathVariable Long prodottoId,
            @RequestParam("prodotto") String prodottoJson,
            @RequestParam(value = "immagine", required = false) MultipartFile immagine) {
        Prodotto prodotto;
        try {
            prodotto = new ObjectMapper().readValue(prodottoJson, Prodotto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Errore durante la deserializzazione del prodotto JSON.", e);
        }


        Prodotto prodottoAggiornato = prodottoService.modificaProdotto(prodottoId, prodotto, immagine);
        return ResponseEntity.ok(prodottoAggiornato);
    }


    @DeleteMapping("/{prodottoId}")
    public ResponseEntity<Void> eliminaProdotto(@PathVariable Long prodottoId) {
        prodottoService.eliminaProdotto(prodottoId);
        return ResponseEntity.noContent().build();
    }
}

