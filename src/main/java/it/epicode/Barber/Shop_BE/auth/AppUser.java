package it.epicode.Barber.Shop_BE.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String nome;

    private String cognome;

    private String avatar;

    private String comuneSalone;

    private String viaSalone;

    private String nomeSalone;

    @JsonProperty("isBarber")
    private boolean isBarber;

    private Integer rangeAppuntamento;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
}


