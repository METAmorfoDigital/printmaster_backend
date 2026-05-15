package com.bpm.printmaster.inventory.controller;
 
import com.bpm.printmaster.inventory.dto.InsumoDTO;
import com.bpm.printmaster.inventory.service.InsumoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/insumos")
@RequiredArgsConstructor
public class InsumoController {
 
    private final InsumoService insumoService;
 
    @GetMapping
    public ResponseEntity<List<InsumoDTO>> getAll() {
        return ResponseEntity.ok(insumoService.getAll());
    }
 
    @PostMapping
    public ResponseEntity<InsumoDTO> save(@RequestBody InsumoDTO dto) {
        return ResponseEntity.ok(insumoService.save(dto));
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<InsumoDTO> update(
            @PathVariable Long id,
            @RequestBody InsumoDTO dto) {
        return ResponseEntity.ok(insumoService.update(id, dto));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        insumoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}