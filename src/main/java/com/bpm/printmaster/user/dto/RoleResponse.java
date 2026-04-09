package com.bpm.printmaster.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class RoleResponse {
    private String value;       // "ADMIN"
    private String descripcion; // "Administrador"
}