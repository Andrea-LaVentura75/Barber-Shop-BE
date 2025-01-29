package it.epicode.Barber.Shop_BE.slotDisponibile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDisponibileDTO {
    private LocalDateTime dataOra;
    private boolean prenotato;
}

