package it.epicode.Barber.Shop_BE.taglio;

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
public class TaglioService {

    private final TaglioRepository taglioRepository;
    private final AppUserRepository appUserRepository;
    private final CloudinaryService cloudinaryService;

    // Posta una nuova foto di un taglio
    public Taglio aggiungiTaglio(Long barbiereId, MultipartFile immagine, String descrizione) {
        AppUser barbiere = appUserRepository.findById(barbiereId)
                .orElseThrow(() -> new EntityNotFoundException("Barbiere non trovato con ID: " + barbiereId));

        if (!barbiere.isBarber()) {
            throw new SecurityException("Solo i barbieri possono postare tagli.");
        }

        String immagineUrl = cloudinaryService.uploader(immagine, "tagli").get("url").toString();

        Taglio taglio = new Taglio();
        taglio.setImmagineUrl(immagineUrl);
        taglio.setDescrizione(descrizione);
        taglio.setBarbiere(barbiere);

        return taglioRepository.save(taglio);
    }

    // Recupera i tagli di un barbiere
    public List<Taglio> trovaTagliPerBarbiere(Long barbiereId) {
        return taglioRepository.findByBarbiereId(barbiereId);
    }

    // Elimina una foto di un taglio
    public void eliminaTaglio(Long taglioId, String usernameBarbiere) {
        Taglio taglio = taglioRepository.findById(taglioId)
                .orElseThrow(() -> new EntityNotFoundException("Taglio non trovato con ID: " + taglioId));

        // Verifica che il taglio appartenga al barbiere autenticato
        if (!taglio.getBarbiere().getUsername().equals(usernameBarbiere)) {
            throw new SecurityException("Non sei autorizzato a eliminare questa foto.");
        }

        taglioRepository.delete(taglio);
    }
}

