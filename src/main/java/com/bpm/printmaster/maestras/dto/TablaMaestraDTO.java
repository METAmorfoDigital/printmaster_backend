package com.bpm.printmaster.maestras.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TablaMaestraDTO {
    private Long id;

    @NotBlank(message = "Categoría requerida")
    private String categoria;

    @NotBlank(message = "Valor requerido")
    private String valor;

    private String descripcion;
    private boolean activo;
}