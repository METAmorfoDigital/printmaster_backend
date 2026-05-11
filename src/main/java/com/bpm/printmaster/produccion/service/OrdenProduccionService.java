package com.bpm.printmaster.produccion.service;

import com.bpm.printmaster.common.Auditoria.AuditLogService;
import com.bpm.printmaster.inventory.entity.Rollo;
import com.bpm.printmaster.inventory.repository.RolloRepository;
import com.bpm.printmaster.produccion.dto.OrdenProduccionDTO;
import com.bpm.printmaster.produccion.dto.DetalleInsigniaDTO;
import com.bpm.printmaster.produccion.dto.OrdenInsigniaRequestDTO;
import com.bpm.printmaster.produccion.dto.OrdenInsigniaResponseDTO;
import com.bpm.printmaster.produccion.entity.*;
import com.bpm.printmaster.produccion.repository.CobradorRepository;
import com.bpm.printmaster.produccion.repository.OrdenProduccionRepository;
import com.bpm.printmaster.produccion.repository.QrCobradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.bpm.printmaster.inventory.service.RolloService;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenProduccionService {

    private final OrdenProduccionRepository ordenRepository;
    private final RolloRepository rolloRepository;
    private final RolloService rolloService;
    private final PagoOrdenService pagoOrdenService;
    private final CobradorRepository cobradorRepository;       // ← nuevo
    private final QrCobradorRepository qrCobradorRepository;   // ← nuevo
    private final AuditLogService auditLogService; //para auditoría

    // ── Listar por tipo ──
    @Transactional(readOnly = true)
    public List<OrdenProduccionDTO> getByTipo(String tipoTrabajo) {
        Class<?> tipoClase = obtenerClase(tipoTrabajo);
        return ordenRepository
            .findByTipoConRollo(tipoClase)
            .stream()
            .map(o -> {
                pagoOrdenService.enriquecerOrden(o);
                return toDTO(o);
            })
            .toList();
    }

    // ── Guardar ──
    @Transactional
    public OrdenProduccionDTO save(OrdenProduccionDTO dto) {
        Rollo rollo = rolloRepository.findById(dto.getRolloId())
            .orElseThrow(() -> new RuntimeException("Rollo no encontrado"));

        OrdenProduccion orden = crearEntidad(dto.getTipoTrabajo());

        int anio = LocalDate.now().getYear();
        int correlativo = siguienteCorrelativo(anio, orden.getClass());

        orden.setCorrelativo(correlativo);
        orden.setAnio(anio);
        orden.setCliente(dto.getCliente());
        orden.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());
        orden.setRollo(rollo);
        orden.setMetraje(dto.getMetraje());
        orden.setCostoImpresion(dto.getCostoImpresion());
        orden.setCantidadPlanchado(dto.getCantidadPlanchado());
        orden.setCostoPlanchado(dto.getCostoPlanchado());
        orden.setCostoDiseno(dto.getCostoDiseno());

        // ← cobrador y QR
        if (dto.getCobradorId() != null) {
            cobradorRepository.findById(dto.getCobradorId())
                .ifPresent(orden::setCobrador);
        }
        if (dto.getQrId() != null && dto.getQrId() > 0) {
            qrCobradorRepository.findById(dto.getQrId())
                .ifPresent(orden::setQr);
        }

        if (dto.getMetraje() != null) {
            rolloService.descontarMetros(dto.getRolloId(), dto.getMetraje());
        }

        OrdenProduccionDTO result = toDTO(ordenRepository.save(orden));
        auditLogService.log("CREATE", dto.getTipoTrabajo(), result.getId().toString(),
            "Nueva orden — Recibo: " + result.getCodigoRecibo() +
            " | Cliente: " + result.getCliente() +
            " | Rollo: " + rollo.getNombre() +
            " | Total: Bs. " + result.getTotal());
        return result;
    }

    // ── Actualizar ──
    @Transactional
    public OrdenProduccionDTO update(Long id, OrdenProduccionDTO dto) {
        OrdenProduccion orden = ordenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (dto.getRolloId() != null) {
            Rollo rollo = rolloRepository.findById(dto.getRolloId())
                .orElseThrow(() -> new RuntimeException("Rollo no encontrado"));
            orden.setRollo(rollo);
        }

        if (dto.getMetraje() != null)           orden.setMetraje(dto.getMetraje());
        if (dto.getCostoImpresion() != null)    orden.setCostoImpresion(dto.getCostoImpresion());
        if (dto.getCantidadPlanchado() != null) orden.setCantidadPlanchado(dto.getCantidadPlanchado());
        if (dto.getCostoPlanchado() != null)    orden.setCostoPlanchado(dto.getCostoPlanchado());
        if (dto.getCostoDiseno() != null)       orden.setCostoDiseno(dto.getCostoDiseno());

        // ← cobrador y QR en edición
        if (dto.getCobradorId() != null) {
            cobradorRepository.findById(dto.getCobradorId())
                .ifPresent(orden::setCobrador);
        }
        if (dto.getQrId() != null && dto.getQrId() > 0) {
            qrCobradorRepository.findById(dto.getQrId())
                .ifPresent(orden::setQr);
        }

        pagoOrdenService.enriquecerOrden(orden);
        OrdenProduccionDTO result = toDTO(ordenRepository.save(orden));
        auditLogService.log("UPDATE", dto.getTipoTrabajo(), result.getId().toString(),
            "Orden actualizada — Recibo: " + result.getCodigoRecibo() +
            " | Cliente: " + result.getCliente() +
            " | Rollo: " + result.getRolloNombre() +
            " | Total: Bs. " + result.getTotal());
        return result;
    }

    // ── Eliminar ──
    @Transactional
    public void delete(Long id) {
        OrdenProduccion orden = ordenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        auditLogService.log("DELETE", "Orden", id.toString(),
            "Orden eliminada — Recibo: " + orden.getCodigoRecibo() +
            " | Cliente: " + orden.getCliente() +
            " | Total: Bs. " + orden.getTotal());
        ordenRepository.deleteById(id);
    }

    // ── Helpers ──
    private int siguienteCorrelativo(int anio, Class<?> tipo) {
        return ordenRepository
            .findMaxCorrelativoByAnioAndTipo(anio, tipo)
            .orElse(0) + 1;
    }

    private OrdenProduccion crearEntidad(String tipoTrabajo) {
        return switch (tipoTrabajo.toUpperCase()) {
            case "DTF"         -> new OrdenDTF();
            case "DTF_PLUS"    -> new OrdenDTFPlus();
            case "SUBLIMADO"   -> new OrdenSublimado();
            case "INSIGNIAS_T" -> new OrdenInsigniasTexturizadas();
            default -> throw new RuntimeException("Tipo de trabajo inválido: " + tipoTrabajo);
        };
    }

    private Class<?> obtenerClase(String tipoTrabajo) {
        return switch (tipoTrabajo.toUpperCase()) {
            case "DTF"         -> OrdenDTF.class;
            case "DTF_PLUS"    -> OrdenDTFPlus.class;
            case "SUBLIMADO"   -> OrdenSublimado.class;
            case "INSIGNIAS_T" -> OrdenInsigniasTexturizadas.class;
            default -> throw new RuntimeException("Tipo de trabajo inválido: " + tipoTrabajo);
        };
    }

    // ── Mapear a DTO ──
    private OrdenProduccionDTO toDTO(OrdenProduccion o) {
        return OrdenProduccionDTO.builder()
            .id(o.getId())
            .correlativo(o.getCorrelativo())
            .anio(o.getAnio())
            .codigoRecibo(o.getCodigoRecibo())
            .cliente(o.getCliente())
            .fecha(o.getFecha())
            .rolloId(o.getRollo() != null ? o.getRollo().getId() : null)
            .rolloNombre(o.getRollo() != null ? o.getRollo().getNombre() : null)
            .metraje(o.getMetraje())
            .costoImpresion(o.getCostoImpresion())
            .subtotalImpresion(o.getSubtotalImpresion())
            .cantidadPlanchado(o.getCantidadPlanchado())
            .costoPlanchado(o.getCostoPlanchado())
            .subtotalPlanchado(o.getSubtotalPlanchado())
            .costoDiseno(o.getCostoDiseno())
            .total(o.getTotal())
            .pagado(o.isPagado())
            .estadoPago(o.getEstadoPago())
            .creadoPor(o.getCreatedBy())
            // ← cobrador y QR
            .cobradorId(o.getCobrador() != null ? o.getCobrador().getId() : null)
            .cobradorNombre(o.getCobrador() != null ? o.getCobrador().getNombre() : null)
            .qrId(o.getQr() != null ? o.getQr().getId() : null)
            .qrBanco(o.getQr() != null ? o.getQr().getBanco() : null)
            .qrImagenBase64(o.getQr() != null ? o.getQr().getImagenBase64() : null)
            .tipoTrabajo(o.getClass()
                .getAnnotation(jakarta.persistence.DiscriminatorValue.class)
                .value())
            .build();
    }

    // ── Guardar insignias ──
    @Transactional
    public OrdenInsigniaResponseDTO saveInsignia(OrdenInsigniaRequestDTO dto) {
        OrdenInsigniasTexturizadas orden = new OrdenInsigniasTexturizadas();

        int anio = LocalDate.now().getYear();
        orden.setCorrelativo(siguienteCorrelativo(anio, OrdenInsigniasTexturizadas.class));
        orden.setAnio(anio);
        orden.setCliente(dto.getCliente());
        orden.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());
        orden.setFechaEntrega(dto.getFechaEntrega());
        orden.setObservaciones(dto.getObservaciones());

        // ← cobrador y QR en insignias
        if (dto.getCobradorId() != null) {
            cobradorRepository.findById(dto.getCobradorId())
                .ifPresent(orden::setCobrador);
        }
        if (dto.getQrId() != null && dto.getQrId() > 0) {
            qrCobradorRepository.findById(dto.getQrId())
                .ifPresent(orden::setQr);
        }

        if (dto.getDetalles() != null) {
            dto.getDetalles().forEach(d -> {
                DetalleInsignia detalle = new DetalleInsignia();
                detalle.setDescripcion(d.getDescripcion());
                detalle.setCantidad(d.getCantidad());
                detalle.setTamano(d.getTamano());
                detalle.setPrecioUnitario(d.getPrecioUnitario());
                orden.addDetalle(detalle);
            });
        }

        OrdenInsigniaResponseDTO result = toInsigniaDTO(ordenRepository.save(orden));
        auditLogService.log("CREATE", "INSIGNIAS_T", result.getId().toString(),
            "Nueva orden insignias — Recibo: " + result.getCodigoRecibo() +
            " | Cliente: " + result.getCliente() +
            " | Total: Bs. " + result.getTotal());
        return result;
    }

    // ── Mapear insignias a DTO ──
    private OrdenInsigniaResponseDTO toInsigniaDTO(OrdenInsigniasTexturizadas o) {
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
            .pagado(o.isPagado())
            .estadoPago(o.getEstadoPago())
            // ← cobrador y QR en insignias
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

} // ← cierre de la clase