package com.bpm.printmaster.produccion.service;

import com.bpm.printmaster.inventory.entity.Rollo;
import com.bpm.printmaster.inventory.repository.RolloRepository;
import com.bpm.printmaster.produccion.dto.OrdenProduccionDTO;
import com.bpm.printmaster.produccion.entity.*;
import com.bpm.printmaster.produccion.repository.OrdenProduccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenProduccionService {

    private final OrdenProduccionRepository ordenRepository;
    private final RolloRepository rolloRepository;

    // ── Listar por tipo ──
  

@Transactional(readOnly = true)
public List<OrdenProduccionDTO> getByTipo(String tipoTrabajo) {
    Class<?> tipoClase = obtenerClase(tipoTrabajo);

    return ordenRepository
        .findByTipo(tipoClase)
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
        orden.setCantidadInsignias(dto.getCantidadInsignias());
        orden.setCostoInsignias(dto.getCostoInsignias());
        orden.setCostoDiseno(dto.getCostoDiseno());

        // ── Pago ──
        orden.setTipoPago(dto.getTipoPago());
        orden.setBanco(dto.getBanco());
        orden.setFechaPago(dto.getFechaPago());

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

    // ── Crear entidad según tipo ──
    private OrdenProduccion crearEntidad(String tipoTrabajo) {
        return switch (tipoTrabajo.toUpperCase()) {
            case "DTF"       -> new OrdenDTF();
            case "DTF_PLUS"  -> new OrdenDTFPlus();
            case "SUBLIMADO" -> new OrdenSublimado();
            default -> throw new RuntimeException("Tipo de trabajo inválido: " + tipoTrabajo);
        };
    }

    // ── Obtener clase según tipo (REUTILIZA crearEntidad) ──
    private Class<?> obtenerClase(String tipoTrabajo) {
        return crearEntidad(tipoTrabajo).getClass();
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
            .cantidadInsignias(o.getCantidadInsignias())
            .costoInsignias(o.getCostoInsignias())
            .subtotalInsignias(o.getSubtotalInsignias())
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
}