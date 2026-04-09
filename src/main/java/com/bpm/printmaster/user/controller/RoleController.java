package com.bpm.printmaster.user.controller;

import com.bpm.printmaster.user.dto.RoleResponse;
import com.bpm.printmaster.user.entity.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class RoleController {

    // ✅ Público — el frontend lo llama sin token
    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        List<RoleResponse> roles = Arrays.stream(Role.values())
                .map(role -> new RoleResponse(role.name(), role.getDescripcion()))
                .toList();
        return ResponseEntity.ok(roles);
    }
}