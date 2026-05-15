package com.bpm.printmaster.inventory.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SalidaMaterialDTO {
    private Long id;
    private String codigoSalida;
    private String tipoTrabajo;
    private String rolloNombre;
    private String rolloCodigo;
    private String rolloNumero;
    private String usuario;
    private LocalDateTime fecha;
    private String nota;
    private List<DetalleDTO> detalles;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DetalleDTO {
        private Long materialId;
        private String materialNombre;
        private String materialTipo; // TINTA, POLIAMIDA, BARNIZ
        private String unidad;       // UNIDAD, KG, LITRO
        private BigDecimal cantidad;
    }
}