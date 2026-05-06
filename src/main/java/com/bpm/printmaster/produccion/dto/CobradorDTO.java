package com.bpm.printmaster.produccion.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CobradorDTO {
    private Long id;
    private String nombre;
    private boolean activo;
    private List<QrCobradorDTO> qrs;
}