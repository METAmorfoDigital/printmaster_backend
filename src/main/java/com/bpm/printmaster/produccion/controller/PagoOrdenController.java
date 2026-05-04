package com.bpm.printmaster.produccion.controller;

import com.bpm.printmaster.produccion.dto.PagoOrdenDTO;
import com.bpm.printmaster.produccion.service.PagoOrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes/{ordenId}/pagos")
@RequiredArgsConstructor
public class PagoOrdenController {

    private final PagoOrdenService pagoService;

    @GetMapping
    public ResponseEntity<List<PagoOrdenDTO>> listar(@PathVariable Long ordenId) {
        return ResponseEntity.ok(pagoService.listarPagos(ordenId));
    }

    @PostMapping
    public ResponseEntity<PagoOrdenDTO> registrar(
            @PathVariable Long ordenId,
            @RequestBody PagoOrdenDTO dto) {
        return ResponseEntity.ok(pagoService.registrarPago(ordenId, dto));
    }

    @DeleteMapping("/{pagoId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long ordenId,
            @PathVariable Long pagoId) {
        pagoService.eliminarPago(pagoId);
        return ResponseEntity.noContent().build();
    }
}