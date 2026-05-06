package com.bpm.printmaster.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CorteRolloDTO {

    @NotNull
    private Long rolloId;

    @NotNull @Min(1)
    private Long ordenId;

    @NotNull
    private Integer itemIndex;

    // En METROS — coherente con metrosDisponibles de la entidad Rollo
    @NotNull @DecimalMin("0.01")
    private Double largoCortadoM;

    // Coordenadas del rectángulo en el canvas (en metros)
    private Double xM;
    private Double yM;
    private Double anchoM;
    private Double altoM;
}