package it.epicode.Barber.Shop_BE.auth;

import it.epicode.Barber.Shop_BE.cloudinary.CloudinaryService;

import it.epicode.Barber.Shop_BE.exceptions.UploadException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Service
public class AppUserService {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public AppUser registerUser(RegisterRequest registerRequest, MultipartFile avatar, Set<Role> roles) {
        // Controllo se l'username è già in uso
        if (appUserRepository.existsByUsername(registerRequest.getUsername())) {
            throw new EntityExistsException("Username già in uso");
       //
        }

        // Crea un nuovo oggetto AppUser
        AppUser appUser = new AppUser();
        BeanUtils.copyProperties(registerRequest, appUser);

        // Codifica la password
        appUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Gestione dell'avatar tramite Cloudinary
        if (avatar != null && !avatar.isEmpty()) {
            try {
                String avatarUrl = cloudinaryService.uploader(avatar, "usersT3").get("url").toString();
                appUser.setAvatar(avatarUrl);
            } catch (Exception e) {
                throw new UploadException("Errore durante il caricamento dell'immagine: " + e.getMessage());
            }
        }

        // Imposta i ruoli
        appUser.setRoles(roles);

        // Imposta i campi specifici per il barbiere
        if (registerRequest.isBarber()) {
            appUser.setComuneSalone(registerRequest.getComuneSalone());
            appUser.setViaSalone(registerRequest.getViaSalone());
            appUser.setNomeSalone(registerRequest.getNomeSalone());
            appUser.setRangeAppuntamento(registerRequest.getRangeAppuntamento());
        } else {
            // Assicurati che i campi specifici per i clienti siano null
            appUser.setComuneSalone(null);
            appUser.setViaSalone(null);
            appUser.setNomeSalone(null);
            appUser.setRangeAppuntamento(null);
        }

        // Salva l'utente nel repository
        return appUserRepository.save(appUser);
    }




    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public AuthResponse authenticateUser(String username, String password)  {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            AppUser appUser = loadUserByUsername(username);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            AuthResponse authResponse = new AuthResponse();
            authResponse.setAccessToken(jwtTokenUtil.generateToken(userDetails) );

            authResponse.setUser(appUser);

            return authResponse;

        } catch (AuthenticationException e) {
            throw new SecurityException("Credenziali non valide", e);
        }
    }


    public AppUser loadUserByUsername(String username)  {
        AppUser appUser = appUserRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));


        return appUser;
    }
}
