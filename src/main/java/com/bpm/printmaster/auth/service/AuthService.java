package com.bpm.printmaster.auth.service;

import com.bpm.printmaster.auth.dto.LoginRequest;
import com.bpm.printmaster.auth.dto.LoginResponse;
import com.bpm.printmaster.auth.dto.RegisterRequest;
import com.bpm.printmaster.user.dto.UserResponse;
import com.bpm.printmaster.user.entity.Role;
import com.bpm.printmaster.user.entity.User;
import com.bpm.printmaster.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        // ✅ Delega validación a Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
            )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

   

    public UserResponse register(RegisterRequest request) {
    // ✅ Verificar si ya existe el usuario
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
        throw new RuntimeException("El usuario ya existe: " 
            + request.getUsername());
    }

    User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.valueOf(request.getRole().toUpperCase()))
            .enabled(true)
            .build();

    User saved = userRepository.save(user);

    // ✅ Retorna DTO, no la entidad
    return UserResponse.builder()
            .id(saved.getId())
            .username(saved.getUsername())
            .role(saved.getRole().name())
            .enabled(saved.isEnabled())
            .build();
}
}