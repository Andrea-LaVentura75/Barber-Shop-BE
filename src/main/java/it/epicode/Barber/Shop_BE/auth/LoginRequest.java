package it.epicode.Barber.Shop_BE.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
