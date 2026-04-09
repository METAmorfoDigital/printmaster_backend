package com.bpm.printmaster.inventory.controller;

import com.bpm.printmaster.inventory.dto.RolloDTO;
import com.bpm.printmaster.inventory.service.RolloService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rollos")
@RequiredArgsConstructor
public class RolloController {

    private final RolloService rolloService;

    @GetMapping
    public ResponseEntity<List<RolloDTO>> getAll() {
        return ResponseEntity.ok(rolloService.getAll());
    }

    @PostMapping
    public ResponseEntity<RolloDTO> save(@Valid @RequestBody RolloDTO dto) {
        return ResponseEntity.ok(rolloService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rolloService.delete(id);
        return ResponseEntity.noContent().build();
    }
}