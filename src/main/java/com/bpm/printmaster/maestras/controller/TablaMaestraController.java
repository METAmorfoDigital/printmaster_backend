package com.bpm.printmaster.maestras.controller;

import com.bpm.printmaster.maestras.dto.TablaMaestraDTO;
import com.bpm.printmaster.maestras.service.TablaMaestraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/maestras")
@RequiredArgsConstructor
public class TablaMaestraController {

    private final TablaMaestraService service;

    // ✅ GET /api/maestras/TIPO_TINTA → lista para el select
    @GetMapping("/{categoria}")
    public ResponseEntity<List<TablaMaestraDTO>> getPorCategoria(
            @PathVariable String categoria) {
        return ResponseEntity.ok(
            service.getPorCategoria(categoria.toUpperCase()));
    }

    // ✅ POST /api/maestras → agregar nuevo valor
    @PostMapping
    public ResponseEntity<TablaMaestraDTO> save(
            @Valid @RequestBody TablaMaestraDTO dto) {
        dto.setCategoria(dto.getCategoria().toUpperCase());
        return ResponseEntity.ok(service.save(dto));
    }

    // ✅ DELETE /api/maestras/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}