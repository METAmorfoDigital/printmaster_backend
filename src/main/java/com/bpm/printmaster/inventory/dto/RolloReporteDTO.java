package com.bpm.printmaster.inventory.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolloReporteDTO {
    private String codigo;
    private String numero;
    private String nombre;
    private String marca;
    private BigDecimal largoTotal;
    private BigDecimal metrosUsados;
    private BigDecimal metrosDisponibles;
    private BigDecimal perdida; // largoTotal - metrosUsados - metrosDisponibles
    private List<UsoRolloDTO> usos;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsoRolloDTO {
        private String codigoOrden;
        private String cliente;
        private String tipoTrabajo;
        private BigDecimal metrosUsados;
        private String fecha;
    }
}