package com.bpm.printmaster.auth.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String role;
}