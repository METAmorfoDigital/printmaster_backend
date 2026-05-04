package com.bpm.printmaster.produccion.controller;


import com.bpm.printmaster.produccion.dto.OrdenProduccionDTO;
import com.bpm.printmaster.produccion.service.OrdenProduccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenProduccionController {

    private final OrdenProduccionService ordenService;

    // GET /api/ordenes?tipo=DTF
    @GetMapping
    public ResponseEntity<List<OrdenProduccionDTO>> getByTipo(
            @RequestParam String tipo) {
        return ResponseEntity.ok(ordenService.getByTipo(tipo));
    }

    // POST /api/ordenes
    @PostMapping
    public ResponseEntity<OrdenProduccionDTO> save(
            @Valid @RequestBody OrdenProduccionDTO dto) {
        return ResponseEntity.ok(ordenService.save(dto));
    }

    // PUT /api/ordenes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<OrdenProduccionDTO> update(
            @PathVariable Long id,
            @RequestBody OrdenProduccionDTO dto) {
        return ResponseEntity.ok(ordenService.update(id, dto));
    }

    // DELETE /api/ordenes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ordenService.delete(id);
        return ResponseEntity.noContent().build();
    }
}