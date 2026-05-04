package com.bpm.printmaster.cliente;

import jakarta.validation.constraints.*;
import lombok.*;
 
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClienteDTO {
 
    private Long id;
 
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 150)
    private String nombre;
 
    @Size(max = 20)
    private String celular;
 
    @Email(message = "Correo inválido")
    @Size(max = 150)
    private String correo;
 
    @Size(max = 250)
    private String direccion;
}
 