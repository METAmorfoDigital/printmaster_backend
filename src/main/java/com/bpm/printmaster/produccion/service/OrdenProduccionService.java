package com.bpm.printmaster.produccion.service;

import com.bpm.printmaster.inventory.entity.Rollo;
import com.bpm.printmaster.inventory.repository.RolloRepository;
import com.bpm.printmaster.produccion.dto.OrdenProduccionDTO;
import com.bpm.printmaster.produccion.dto.DetalleInsigniaDTO;
import com.bpm.printmaster.produccion.dto.OrdenInsigniaRequestDTO;
import com.bpm.printmaster.produccion.dto.OrdenInsigniaResponseDTO;
import com.bpm.printmaster.produccion.entity.*;
import com.bpm.printmaster.produccion.repository.OrdenProduccionRepository;
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

    // ── Listar por tipo ──
  

@Transactional(readOnly = true)
public List<OrdenProduccionDTO> getByTipo(String tipoTrabajo) {
    Class<?> tipoClase = obtenerClase(tipoTrabajo);

    return ordenRepository
        .findByTipoConRollo(tipoClase)
        .stream()
        .map(this::toDTO)
        .toList();
}

    // ── Guardar ──
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

        // ── Producción ──
        orden.setMetraje(dto.getMetraje());
        orden.setCostoImpresion(dto.getCostoImpresion());
        orden.setCantidadPlanchado(dto.getCantidadPlanchado());
        orden.setCostoPlanchado(dto.getCostoPlanchado());

        orden.setCostoDiseno(dto.getCostoDiseno());

        // ── Pago ──
        orden.setTipoPago(dto.getTipoPago());
        orden.setBanco(dto.getBanco());
        orden.setFechaPago(dto.getFechaPago());

        // ← AGREGAR: descuenta metros del rollo
        if (dto.getMetraje() != null) {
            rolloService.descontarMetros(dto.getRolloId(), dto.getMetraje());
        }

        return toDTO(ordenRepository.save(orden));
    }

    // ── Actualizar orden DTF/DTF+/Sublimado ──
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
        if (dto.getTipoPago() != null)          orden.setTipoPago(dto.getTipoPago());
        if (dto.getBanco() != null)             orden.setBanco(dto.getBanco());
        if (dto.getFechaPago() != null)         orden.setFechaPago(dto.getFechaPago());

        return toDTO(ordenRepository.save(orden));
    }

    // ── Eliminar ──
    public void delete(Long id) {
        ordenRepository.deleteById(id);
    }

    // ── Siguiente correlativo por año y tipo ──
    private int siguienteCorrelativo(int anio, Class<?> tipo) {
        return ordenRepository
            .findMaxCorrelativoByAnioAndTipo(anio, tipo)
            .orElse(0) + 1;
    }

    private OrdenProduccion crearEntidad(String tipoTrabajo) {
    return switch (tipoTrabajo.toUpperCase()) {
        case "DTF"        -> new OrdenDTF();
        case "DTF_PLUS"   -> new OrdenDTFPlus();
        case "SUBLIMADO"  -> new OrdenSublimado();
        case "INSIGNIAS_T" -> new OrdenInsigniasTexturizadas();  // ← esto faltaba
        default -> throw new RuntimeException("Tipo de trabajo inválido: " + tipoTrabajo);
    };
}

private Class<?> obtenerClase(String tipoTrabajo) {
    return switch (tipoTrabajo.toUpperCase()) {
        case "DTF"         -> OrdenDTF.class;
        case "DTF_PLUS"    -> OrdenDTFPlus.class;
        case "SUBLIMADO"   -> OrdenSublimado.class;
        case "INSIGNIAS_T" -> OrdenInsigniasTexturizadas.class;  // ← y esto
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
            .tipoPago(o.getTipoPago())
            .banco(o.getBanco())
            .fechaPago(o.getFechaPago())
            .pagado(o.isPagado())
            .tipoTrabajo(o.getClass()
                .getAnnotation(jakarta.persistence.DiscriminatorValue.class)
                .value())
            .build();
    }


    // ── Guardar insignias texturizadas ──  ← AGREGAR DESDE AQUÍ
    public OrdenInsigniaResponseDTO saveInsignia(OrdenInsigniaRequestDTO dto) {
        OrdenInsigniasTexturizadas orden = new OrdenInsigniasTexturizadas();

        int anio = LocalDate.now().getYear();
        orden.setCorrelativo(siguienteCorrelativo(anio, OrdenInsigniasTexturizadas.class));
        orden.setAnio(anio);
        orden.setCliente(dto.getCliente());
        orden.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());
        orden.setFechaEntrega(dto.getFechaEntrega());
        orden.setObservaciones(dto.getObservaciones());
        orden.setTipoPago(dto.getTipoPago());
        orden.setBanco(dto.getBanco());
        orden.setFechaPago(dto.getFechaPago());

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

        return toInsigniaDTO(ordenRepository.save(orden));
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
            .tipoPago(o.getTipoPago())
            .banco(o.getBanco())
            .fechaPago(o.getFechaPago())
            .pagado(o.isPagado())
            .detalles(o.getDetalles().stream()
            .map(d -> {
                DetalleInsigniaDTO dto = new DetalleInsigniaDTO();
                dto.setId(d.getId());
                dto.setDescripcion(d.getDescripcion());
                dto.setTamano(d.getTamano());
                dto.setCantidad(d.getCantidad());
                dto.setPrecioUnitario(d.getPrecioUnitario());
                dto.setSubtotal(d.getSubtotal());
                return dto;
            })
        .toList())
            .build();
    }

} // ← cierre de la clase