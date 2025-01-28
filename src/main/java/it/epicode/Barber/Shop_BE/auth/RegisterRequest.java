package it.epicode.Barber.Shop_BE.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String nome;
    private String cognome;

    // Campi barbieri
    private String comuneSalone;
    private String viaSalone;
    private String nomeSalone;
    private Integer rangeAppuntamento;

    // Booleano per il tipo di utente
    private boolean isBarber;
}


