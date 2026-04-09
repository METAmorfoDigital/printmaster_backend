package com.bpm.printmaster.auth.dto;

import jakarta.validation.constraints.NotBlank;  // Spring Boot 3+
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank 
    private String username;

    @NotBlank 
    private String password;

    @NotBlank
    private String role;
}