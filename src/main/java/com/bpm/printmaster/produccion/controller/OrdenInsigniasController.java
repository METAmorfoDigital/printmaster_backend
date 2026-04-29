package com.bpm.printmaster.produccion.controller;

import com.bpm.printmaster.produccion.dto.OrdenInsigniaRequestDTO;
import com.bpm.printmaster.produccion.dto.OrdenInsigniaResponseDTO;
import com.bpm.printmaster.produccion.service.OrdenInsigniasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes/insignias")
@RequiredArgsConstructor
public class OrdenInsigniasController {

    private final OrdenInsigniasService ordenInsigniasService;

    @GetMapping
    public ResponseEntity<List<OrdenInsigniaResponseDTO>> listar() {
        return ResponseEntity.ok(ordenInsigniasService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenInsigniaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenInsigniasService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<OrdenInsigniaResponseDTO> crear(
            @RequestBody OrdenInsigniaRequestDTO dto) {
        return ResponseEntity.ok(ordenInsigniasService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenInsigniaResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody OrdenInsigniaRequestDTO dto) {
        return ResponseEntity.ok(ordenInsigniasService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ordenInsigniasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}