package com.bpm.printmaster.produccion.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PagoOrdenDTO {
    private Long id;
    private Long ordenId;
    private BigDecimal monto;
    private String tipoPago;
    private String banco;
    private LocalDate fechaPago;
    private String nota;
}