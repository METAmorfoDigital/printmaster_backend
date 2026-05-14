package com.bpm.printmaster.produccion.controller;

import com.bpm.printmaster.produccion.dto.CobradorDTO;
import com.bpm.printmaster.produccion.dto.QrCobradorDTO;
import com.bpm.printmaster.produccion.service.CobradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cobradores")
@RequiredArgsConstructor
public class CobradorController {

    private final CobradorService cobradorService;

    // ── Cobradores — todos los autenticados pueden ver ──
    @GetMapping
    public ResponseEntity<List<CobradorDTO>> listar() {
        return ResponseEntity.ok(cobradorService.listarActivos());
    }

    @GetMapping("/todos")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<CobradorDTO>> listarTodos() {
        return ResponseEntity.ok(cobradorService.listarTodos());
    }

    // ── Solo ADMIN puede modificar ──
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CobradorDTO> crear(@RequestBody CobradorDTO dto) {
        return ResponseEntity.ok(cobradorService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CobradorDTO> actualizar(
            @PathVariable Long id,
            @RequestBody CobradorDTO dto) {
        return ResponseEntity.ok(cobradorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cobradorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ── QR ──
    @PostMapping("/{cobradorId}/qr")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<QrCobradorDTO> agregarQr(
            @PathVariable Long cobradorId,
            @RequestBody QrCobradorDTO dto) {
        return ResponseEntity.ok(cobradorService.agregarQr(cobradorId, dto));
    }

    @DeleteMapping("/qr/{qrId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> eliminarQr(@PathVariable Long qrId) {
        cobradorService.eliminarQr(qrId);
        return ResponseEntity.noContent().build();
    }

    // ── Alertas ──
    @GetMapping("/qr/por-expirar")
    public ResponseEntity<List<QrCobradorDTO>> qrPorExpirar() {
        return ResponseEntity.ok(cobradorService.qrPorExpirar());
    }
}