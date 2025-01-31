package it.epicode.Barber.Shop_BE.appuntamento;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppuntamentoDTO {
    private Long id;
    private String clienteNome;
    private String barbiereNome;
    private LocalDateTime dataOra;
    private String nota;
    private String servizioNome;
    private Double servizioPrezzo;
}



