// OrdenInsigniaRequestDTO.java  — para crear/editar
package com.bpm.printmaster.produccion.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrdenInsigniaRequestDTO {
    private String cliente;
    private LocalDate fecha;
    private LocalDate fechaEntrega;
    private String observaciones;
    private String tipoPago;
    private String banco;
    private LocalDate fechaPago;
    private List<DetalleInsigniaDTO> detalles;
}