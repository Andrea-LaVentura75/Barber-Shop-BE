package it.epicode.Barber.Shop_BE.appuntamento;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppuntamentoDTO {
    private Long id;
    private String clienteNome;
    private String barbiereNome;
    private LocalDateTime dataOra;
    private String nota;

    // Aggiungi i campi del servizio
    private String servizioNome;
    private Double servizioPrezzo;
}


