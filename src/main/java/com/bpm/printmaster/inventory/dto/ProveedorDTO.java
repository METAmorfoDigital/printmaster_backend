package com.bpm.printmaster.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorDTO {
    private Long id;

    @NotBlank(message = "Nombre requerido")
    private String nombre;

    @NotBlank(message = "Teléfono requerido")
    private String telefono;

    @Email(message = "Email inválido")
    private String email;

    private String direccion;

    private Boolean activo; // ✅ Boolean con mayúscula
}