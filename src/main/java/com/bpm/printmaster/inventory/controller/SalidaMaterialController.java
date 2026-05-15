package com.bpm.printmaster.inventory.controller;
 
import com.bpm.printmaster.inventory.dto.SalidaMaterialDTO;
import com.bpm.printmaster.inventory.service.SalidaMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/salidas")
@RequiredArgsConstructor
public class SalidaMaterialController {
 
    private final SalidaMaterialService salidaService;
 
    @GetMapping
    public ResponseEntity<List<SalidaMaterialDTO>> getAll(
            @RequestParam(required = false) String tipoTrabajo) {
        if (tipoTrabajo != null)
            return ResponseEntity.ok(salidaService.getByTipoTrabajo(tipoTrabajo));
        return ResponseEntity.ok(salidaService.getAll());
    }
 
    @PostMapping
    public ResponseEntity<SalidaMaterialDTO> registrar(
            @RequestBody SalidaMaterialDTO dto) {
        return ResponseEntity.ok(salidaService.registrar(dto));
    }
}