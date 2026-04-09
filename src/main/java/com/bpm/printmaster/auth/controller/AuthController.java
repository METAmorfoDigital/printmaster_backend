package com.bpm.printmaster.auth.controller;

import com.bpm.printmaster.auth.dto.LoginRequest;
import com.bpm.printmaster.auth.dto.LoginResponse;
import com.bpm.printmaster.auth.dto.RegisterRequest;
import com.bpm.printmaster.auth.service.AuthService;
import com.bpm.printmaster.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = 
        LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        logger.info("Registro de nuevo usuario: {}", request.getUsername());
        UserResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        logger.info("Intento de login: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        logger.info("Login exitoso: {} con rol {}", 
            request.getUsername(), response.getRole());
        return ResponseEntity.ok(response);
    }
}