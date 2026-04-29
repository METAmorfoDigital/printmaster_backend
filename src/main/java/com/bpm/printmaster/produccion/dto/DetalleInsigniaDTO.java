// DetalleInsigniaDTO.java
package com.bpm.printmaster.produccion.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DetalleInsigniaDTO {
    private Long id;
    private String descripcion;
    private String tamano;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;       // calculado, solo lectura
}