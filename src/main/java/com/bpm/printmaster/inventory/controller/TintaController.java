package com.bpm.printmaster.inventory.controller;

import com.bpm.printmaster.inventory.dto.ConsumoTintaDTO;
import com.bpm.printmaster.inventory.dto.TintaDTO;
import com.bpm.printmaster.inventory.service.TintaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

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

   @PutMapping("/{id}")
public ResponseEntity<TintaDTO> update(
        @PathVariable Long id,
        @RequestBody TintaDTO dto) {  // ← sin @Valid
    return ResponseEntity.ok(tintaService.update(id, dto));
}

    @PutMapping("/{id}/descontar")
    public ResponseEntity<TintaDTO> descontar(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String tipoTrabajo = body != null ? body.get("tipoTrabajo") : null;
        String nota        = body != null ? body.get("nota") : null;
        return ResponseEntity.ok(tintaService.descontarStock(id, tipoTrabajo, nota));
    }

    @GetMapping("/consumos")
    public ResponseEntity<List<ConsumoTintaDTO>> getConsumos(
            @RequestParam(required = false) String tipoTrabajo,
            @RequestParam(required = false) Long tintaId) {
        return ResponseEntity.ok(tintaService.getConsumos(tipoTrabajo, tintaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tintaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}