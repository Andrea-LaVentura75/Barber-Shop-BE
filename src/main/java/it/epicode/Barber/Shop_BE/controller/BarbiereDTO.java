package it.epicode.Barber.Shop_BE.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarbiereDTO {
    private Long id;
    private String nome;
    private String cognome;
    private String nomeSalone;
    private String avatar;
}

