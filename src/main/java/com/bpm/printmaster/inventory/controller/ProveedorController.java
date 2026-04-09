package com.bpm.printmaster.inventory.controller;

import com.bpm.printmaster.inventory.dto.ProveedorDTO;
import com.bpm.printmaster.inventory.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> getAll() {
        return ResponseEntity.ok(proveedorService.getAll());
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> save(
            @Valid @RequestBody ProveedorDTO dto) {
        return ResponseEntity.ok(proveedorService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorDTO dto) {
        return ResponseEntity.ok(proveedorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proveedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}