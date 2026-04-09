package com.bpm.printmaster.inventory.controller;

import com.bpm.printmaster.inventory.dto.TintaDTO;
import com.bpm.printmaster.inventory.service.TintaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tintas")
@RequiredArgsConstructor
public class TintaController {

    private final TintaService tintaService;

    @GetMapping
    public ResponseEntity<List<TintaDTO>> getAll() {
        return ResponseEntity.ok(tintaService.getAll());
    }

    @PostMapping
    public ResponseEntity<TintaDTO> save(@Valid @RequestBody TintaDTO dto) {
        return ResponseEntity.ok(tintaService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tintaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}