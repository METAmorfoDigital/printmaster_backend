package com.bpm.printmaster.produccion.service;

import com.bpm.printmaster.common.Auditoria.AuditLogService;
import com.bpm.printmaster.produccion.dto.PagoOrdenDTO;
import com.bpm.printmaster.produccion.entity.OrdenProduccion;
import com.bpm.printmaster.produccion.entity.PagoOrden;
import com.bpm.printmaster.produccion.repository.OrdenProduccionRepository;
import com.bpm.printmaster.produccion.repository.PagoOrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoOrdenService {

    private final PagoOrdenRepository pagoRepository;
    private final OrdenProduccionRepository ordenRepository;
    private final AuditLogService auditLogService;


    @Transactional(readOnly = true)
    public List<PagoOrdenDTO> listarPagos(Long ordenId) {
        return pagoRepository.findByOrdenIdOrderByFechaPagoAsc(ordenId)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Transactional
    public PagoOrdenDTO registrarPago(Long ordenId, PagoOrdenDTO dto) {
        OrdenProduccion orden = ordenRepository.findById(ordenId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + ordenId));

        BigDecimal sumaPagada = pagoRepository.sumMontoByOrdenId(ordenId);
        BigDecimal saldoPendiente = orden.getTotal().subtract(sumaPagada);

        if (dto.getMonto().compareTo(saldoPendiente) > 0) {
            throw new RuntimeException("El monto supera el saldo pendiente de Bs. " + saldoPendiente);
        }

        PagoOrden pago = new PagoOrden();
        pago.setOrden(orden);
        pago.setMonto(dto.getMonto());
        pago.setTipoPago(dto.getTipoPago());
        pago.setBanco(dto.getBanco());
        pago.setFechaPago(dto.getFechaPago());
        pago.setNota(dto.getNota());

        PagoOrdenDTO result = toDTO(pagoRepository.save(pago));
        auditLogService.log("CREATE", "Pago", result.getId().toString(),
            "Pago registrado — Orden: " + orden.getCodigoRecibo() +
            " | Cliente: " + orden.getCliente() +
            " | Monto: Bs. " + result.getMonto() +
            " | Tipo: " + result.getTipoPago() +
            (result.getBanco() != null ? " | Banco: " + result.getBanco() : ""));
        return result;
    }

    @Transactional
    public void eliminarPago(Long pagoId) {
        PagoOrden pago = pagoRepository.findById(pagoId)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + pagoId));
        auditLogService.log("DELETE", "Pago", pagoId.toString(),
            "Pago eliminado — Orden: " + pago.getOrden().getCodigoRecibo() +
            " | Cliente: " + pago.getOrden().getCliente() +
            " | Monto: Bs. " + pago.getMonto() +
            " | Tipo: " + pago.getTipoPago());
        pagoRepository.deleteById(pagoId);
    }

    public String calcularEstado(Long ordenId, BigDecimal total) {
        BigDecimal sumaPagada = pagoRepository.sumMontoByOrdenId(ordenId);
        if (sumaPagada.compareTo(BigDecimal.ZERO) == 0) return "PENDIENTE";
        if (sumaPagada.compareTo(total) >= 0)           return "PAGADO";
        return "PARCIAL";
    }

    private PagoOrdenDTO toDTO(PagoOrden p) {
        return PagoOrdenDTO.builder()
            .id(p.getId())
            .ordenId(p.getOrden().getId())
            .monto(p.getMonto())
            .tipoPago(p.getTipoPago())
            .banco(p.getBanco())
            .fechaPago(p.getFechaPago())
            .nota(p.getNota())
            .build();
    }

    
    public void enriquecerOrden(OrdenProduccion orden) {
    BigDecimal suma = pagoRepository.sumMontoByOrdenId(orden.getId());
    orden.setSumaPagos(suma);
    if (suma.compareTo(BigDecimal.ZERO) == 0) {
        orden.setEstadoPago("PENDIENTE");
    } else if (orden.getTotal() != null && suma.compareTo(orden.getTotal()) >= 0) {
        orden.setEstadoPago("PAGADO");
    } else {
        orden.setEstadoPago("PARCIAL");
    }
}
}