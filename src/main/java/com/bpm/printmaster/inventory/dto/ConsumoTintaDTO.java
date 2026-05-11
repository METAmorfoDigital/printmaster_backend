package com.bpm.printmaster.inventory.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsumoTintaDTO {
    private Long id;
    private Long tintaId;
    private String tintaNombre;
    private String tintaColor;
    private String tipoTrabajo;
    private String rolloNombre;
    private String rolloCodigo;
    private String usuario;
    private LocalDateTime fecha;
    private String nota;
}