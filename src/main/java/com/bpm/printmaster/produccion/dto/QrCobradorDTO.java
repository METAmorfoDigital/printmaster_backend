package com.bpm.printmaster.produccion.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QrCobradorDTO {
    private Long id;
    private Long cobradorId;
    private String banco;
    private String imagenBase64;
    private LocalDate fechaExpiracion;
    private boolean porExpirar; // true si expira en 7 días o menos
}