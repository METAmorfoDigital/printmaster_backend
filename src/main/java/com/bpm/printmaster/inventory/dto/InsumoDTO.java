package com.bpm.printmaster.inventory.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InsumoDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private BigDecimal cantidad; // stock en kg o litros
    private String unidad;       // KG, LITRO
    private String tipoInsumo;   // POLIAMIDA, BARNIZ
    private String marca;
    private String tipoTrabajo;
    private Long proveedorId;
}