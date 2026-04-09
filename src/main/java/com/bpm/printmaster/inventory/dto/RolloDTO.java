package com.bpm.printmaster.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolloDTO {
    private Long id;

    @NotBlank(message = "Nombre requerido")
    private String nombre;

    @NotNull(message = "Cantidad requerida")
    private Integer cantidad;

    @NotBlank(message = "Marca requerida")
    private String marca;


    @NotNull(message = "Precio requerido")
    private BigDecimal precio;

    @NotNull(message = "Largo requerido")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal largo;

    @NotNull(message = "Ancho requerido")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal ancho;

    @NotNull(message = "Tipo de rollo requerido")
    private Long tipoRolloId;

    @NotNull(message = "Proveedor requerido")
    private Long proveedorId;
}