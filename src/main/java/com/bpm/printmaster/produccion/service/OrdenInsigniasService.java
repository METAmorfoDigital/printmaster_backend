package com.bpm.printmaster.produccion.service;

import com.bpm.printmaster.common.Auditoria.AuditLogService;
import com.bpm.printmaster.produccion.dto.*;
import com.bpm.printmaster.produccion.entity.*;
import com.bpm.printmaster.produccion.repository.CobradorRepository;
import com.bpm.printmaster.produccion.repository.OrdenProduccionRepository;
import com.bpm.printmaster.produccion.repository.QrCobradorRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenInsigniasService {

    private final OrdenProduccionRepository ordenRepository;
    private final PagoOrdenService pagoOrdenService;
    private final CobradorRepository cobradorRepository;     
    private final QrCobradorRepository qrCobradorRepository;   
    private final AuditLogService auditLogService; //para auditoría

    @Transactional(readOnly = true)
    public List<OrdenInsigniaResponseDTO> listar() {
        return ordenRepository
            .findByTipoConDetalles(OrdenInsigniasTexturizadas.class)
            .stream()
            .map(o -> {
            pagoOrdenService.enriquecerOrden(o); // ← agregar
            return toResponse((OrdenInsigniasTexturizadas) o);
        })
            .toList();
    }

    @Transactional(readOnly = true)
    public OrdenInsigniaResponseDTO buscarPorId(Long id) {
        OrdenInsigniasTexturizadas orden = buscar(id);
        return toResponse(orden);
    }

    @Transactional
    public OrdenInsigniaResponseDTO crear(OrdenInsigniaRequestDTO dto) {
        OrdenInsigniasTexturizadas orden = new OrdenInsigniasTexturizadas();
        mapearCampos(orden, dto);

        int anio = LocalDate.now().getYear();
        orden.setAnio(anio);
        orden.setCorrelativo(siguienteCorrelativo(anio));

        OrdenInsigniasTexturizadas guardada = ordenRepository.save(orden); // ← solo una vez
        OrdenInsigniaResponseDTO result = toResponse(guardada);

        auditLogService.log("CREATE", "Insignias", result.getId().toString(),
            "Nueva orden " + result.getCodigoRecibo() + " — Cliente: " + result.getCliente());

        return result; // ← retornar el resultado ya mapeado
    }

    @Transactional
    public OrdenInsigniaResponseDTO actualizar(Long id, OrdenInsigniaRequestDTO dto) {
        OrdenInsigniasTexturizadas orden = buscar(id);
        orden.clearDetalles();
        mapearCampos(orden, dto);

        OrdenInsigniasTexturizadas guardada = ordenRepository.save(orden); // ← solo una vez
        OrdenInsigniaResponseDTO result = toResponse(guardada);

        auditLogService.log("UPDATE", "Insignias", id.toString(),
            "Orden editada" + result.getCodigoRecibo() + " — Cliente: " + result.getCliente());

        return result;
    }

    @Transactional
    public void eliminar(Long id) {
        OrdenInsigniasTexturizadas orden = buscar(id); // ← obtener datos antes de borrar
        
        auditLogService.log("DELETE", "Insignias", id.toString(),
            "Orden eliminada — Recibo: " + orden.getCodigoRecibo() +
            " | Cliente: " + orden.getCliente() +
            " | Total: Bs. " + orden.getTotal() +
            " | Fecha: " + orden.getFecha()
            );
        ordenRepository.deleteById(id);
    }

    // ── Privados ────────────────────────────────────────────────

    private void mapearCampos(OrdenInsigniasTexturizadas orden, OrdenInsigniaRequestDTO dto) {
        orden.setCliente(dto.getCliente());
        orden.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());
        orden.setFechaEntrega(dto.getFechaEntrega());
        orden.setObservaciones(dto.getObservaciones());
        orden.setTipoPago(dto.getTipoPago());
        orden.setBanco(dto.getBanco());
        orden.setFechaPago(dto.getFechaPago());

        // ← agregar estas dos secciones
        if (dto.getCobradorId() != null) {
            cobradorRepository.findById(dto.getCobradorId())
                .ifPresent(orden::setCobrador);
        } else {
            orden.setCobrador(null);
        }

        if (dto.getQrId() != null && dto.getQrId() > 0) {
            qrCobradorRepository.findById(dto.getQrId())
                .ifPresent(orden::setQr);
        } else {
            orden.setQr(null);
        }

        if (dto.getDetalles() != null) {
            dto.getDetalles().forEach(d -> {
                DetalleInsignia detalle = new DetalleInsignia();
                detalle.setDescripcion(d.getDescripcion());
                detalle.setTamano(d.getTamano());
                detalle.setCantidad(d.getCantidad());
                detalle.setPrecioUnitario(d.getPrecioUnitario());
                orden.addDetalle(detalle);
            });
        }
    }

    private OrdenInsigniasTexturizadas buscar(Long id) {
        OrdenProduccion orden = ordenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + id));
        if (!(orden instanceof OrdenInsigniasTexturizadas insignia)) {
            throw new RuntimeException("La orden " + id + " no es de tipo insignias");
        }
        return insignia;
    }

    private int siguienteCorrelativo(int anio) {
        return ordenRepository
            .findMaxCorrelativoByAnioAndTipo(anio, OrdenInsigniasTexturizadas.class)
            .orElse(0) + 1;
    }

    private OrdenInsigniaResponseDTO toResponse(OrdenInsigniasTexturizadas o) {
        return OrdenInsigniaResponseDTO.builder()
            .id(o.getId())
            .correlativo(o.getCorrelativo())
            .anio(o.getAnio())
            .codigoRecibo(o.getCodigoRecibo())
            .cliente(o.getCliente())
            .fecha(o.getFecha())
            .fechaEntrega(o.getFechaEntrega())
            .observaciones(o.getObservaciones())
            .total(o.getTotal())
            .tipoPago(o.getTipoPago())
            .banco(o.getBanco())
            .fechaPago(o.getFechaPago())
            .estadoPago(o.getEstadoPago())  // ← agregar esta línea
            .pagado(o.isPagado())
            .cobradorId(o.getCobrador() != null ? o.getCobrador().getId() : null)
            .cobradorNombre(o.getCobrador() != null ? o.getCobrador().getNombre() : null)
            .qrId(o.getQr() != null ? o.getQr().getId() : null)
            .qrBanco(o.getQr() != null ? o.getQr().getBanco() : null)
            .qrImagenBase64(o.getQr() != null ? o.getQr().getImagenBase64() : null)
            .creadoPor(o.getCreatedBy())
            .detalles(o.getDetalles().stream()
                .map(d -> {
                    DetalleInsigniaDTO det = new DetalleInsigniaDTO();
                    det.setId(d.getId());
                    det.setDescripcion(d.getDescripcion());
                    det.setTamano(d.getTamano());
                    det.setCantidad(d.getCantidad());
                    det.setPrecioUnitario(d.getPrecioUnitario());
                    det.setSubtotal(d.getSubtotal());
                    return det;
                })
                .toList())
            .build();
    }
}