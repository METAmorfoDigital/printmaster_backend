package com.bpm.printmaster.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TintaDTO {
    private Long id;

    @NotBlank(message = "Nombre requerido")
    private String nombre;

    @NotNull(message = "Precio requerido")
    private BigDecimal precio;

    private Integer cantidadAgregar; // se suma al stock existente

    @NotNull(message = "Cantidad requerida")
    @Min(value = 0)
    private Integer cantidad;

    private String tipoTrabajo;  // DTF | DTF_PLUS | SUBLIMADO | INSIGNIAS_T



    @NotBlank(message = "Color requerido")
    private String color;

    private String marca;

    @NotNull(message = "Tipo de tinta requerido")
    private Long tipoTintaId;  // ✅ Long no String

    @NotNull(message = "Proveedor requerido")
    private Long proveedorId;
}