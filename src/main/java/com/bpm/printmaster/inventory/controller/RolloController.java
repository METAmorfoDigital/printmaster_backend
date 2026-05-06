package com.bpm.printmaster.inventory.controller;

import com.bpm.printmaster.inventory.dto.RolloDTO;
import com.bpm.printmaster.inventory.dto.RolloReporteDTO;
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
public ResponseEntity<?> delete(@PathVariable Long id) {
    try {
        rolloService.delete(id);
        return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
@GetMapping("/{id}/reporte")
public ResponseEntity<RolloReporteDTO> getReporte(@PathVariable Long id) {
    return ResponseEntity.ok(rolloService.getReporte(id));
}

    @PutMapping("/{id}/stock")
public ResponseEntity<RolloDTO> agregarStock(
        @PathVariable Long id,
        @RequestParam Integer cantidad) {

    return ResponseEntity.ok(rolloService.agregarStock(id, cantidad));
}

@PostMapping("/lote")
public ResponseEntity<List<RolloDTO>> saveLote(@RequestBody RolloDTO dto) {
    return ResponseEntity.ok(rolloService.saveLote(dto));
}
}