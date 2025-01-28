package it.epicode.Barber.Shop_BE.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;

    @PostMapping(path = "/register", consumes = {"multipart/form-data"})
    @PreAuthorize("permitAll()")
    public ResponseEntity<AppUser> register(@RequestParam("appUser") String appUser,
                                            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        System.out.println("Endpoint /register raggiunto con dati: " + appUser);

        ObjectMapper objectMapper = new ObjectMapper();
        RegisterRequest registerRequest;

        try {
            // Tentativo di deserializzazione
            registerRequest = objectMapper.readValue(appUser, RegisterRequest.class);
            System.out.println("Dati deserializzati con successo: " + registerRequest);
        } catch (JsonProcessingException e) {
            // Log di errore dettagliato
            System.err.println("Errore nella deserializzazione: " + e.getMessage());
            throw new RuntimeException("Errore nella deserializzazione dei dati", e);
        }

        // Determina i ruoli in base al valore di isBarber
        Set<Role> roles = registerRequest.isBarber() ? Set.of(Role.ROLE_BARBER) : Set.of(Role.ROLE_CLIENT);

        // Passa al service per registrare l'utente
        AppUser registeredUser = appUserService.registerUser(registerRequest, avatar, roles);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = appUserService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(authResponse);
    }
}

