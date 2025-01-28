package it.epicode.Barber.Shop_BE.prodotto;

import it.epicode.Barber.Shop_BE.auth.AppUser;
import it.epicode.Barber.Shop_BE.auth.AppUserRepository;
import it.epicode.Barber.Shop_BE.cloudinary.CloudinaryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;
    private final AppUserRepository appUserRepository;
    private final CloudinaryService cloudinaryService;

    // Aggiungi un prodotto
    public Prodotto aggiungiProdotto(Long barbiereId, Prodotto prodotto, MultipartFile immagine) {
        AppUser barbiere = appUserRepository.findById(barbiereId)
                .orElseThrow(() -> new EntityNotFoundException("Barbiere non trovato con ID: " + barbiereId));

        if (!barbiere.isBarber()) {
            throw new SecurityException("Solo i barbieri possono aggiungere prodotti.");
        }

        prodotto.setBarbiere(barbiere);

        // Carica l'immagine su Cloudinary
        if (immagine != null && !immagine.isEmpty()) {
            String immagineUrl = cloudinaryService.uploader(immagine, "prodotti").get("url").toString();
            prodotto.setImmagineUrl(immagineUrl);
        }

        return prodottoRepository.save(prodotto);
    }

    // Recupera i prodotti di un barbiere
    public List<Prodotto> trovaProdottiPerBarbiere(Long barbiereId) {
        return prodottoRepository.findByBarbiereId(barbiereId);
    }

    // Modifica un prodotto
    public Prodotto modificaProdotto(Long prodottoId, Prodotto prodotto, MultipartFile immagine) {
        Prodotto prodottoEsistente = prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato con ID: " + prodottoId));

        prodottoEsistente.setNome(prodotto.getNome());
        prodottoEsistente.setDescrizione(prodotto.getDescrizione());
        prodottoEsistente.setPrezzo(prodotto.getPrezzo());

        // Carica l'immagine aggiornata su Cloudinary
        if (immagine != null && !immagine.isEmpty()) {
            String immagineUrl = cloudinaryService.uploader(immagine, "prodotti").get("url").toString();
            prodottoEsistente.setImmagineUrl(immagineUrl);
        }

        return prodottoRepository.save(prodottoEsistente);
    }

    // Rimuovi un prodotto
    public void eliminaProdotto(Long prodottoId) {
        Prodotto prodotto = prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato con ID: " + prodottoId));
        prodottoRepository.delete(prodotto);
    }
}

