package com.bpm.printmaster.inventory.service;

import com.bpm.printmaster.inventory.dto.RolloDTO;
import com.bpm.printmaster.inventory.dto.RolloReporteDTO;
import com.bpm.printmaster.inventory.entity.Rollo;
import com.bpm.printmaster.inventory.entity.Proveedor;
import com.bpm.printmaster.maestras.entity.TablaMaestra;
import com.bpm.printmaster.inventory.repository.RolloRepository;
import com.bpm.printmaster.inventory.repository.ProveedorRepository;
import com.bpm.printmaster.maestras.repository.TablaMaestraRepository;
import com.bpm.printmaster.produccion.entity.OrdenProduccion;
import com.bpm.printmaster.produccion.repository.OrdenProduccionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RolloService {

    private final RolloRepository rolloRepository;
    private final ProveedorRepository proveedorRepository;
    private final TablaMaestraRepository tablaMaestraRepository;
    private final OrdenProduccionRepository ordenRepository;


    public List<RolloDTO> getAll() {
        return rolloRepository.findAll()
                .stream().map(this::toDTO).toList();
    }

    public RolloDTO save(RolloDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        TablaMaestra tipoRollo = tablaMaestraRepository.findById(dto.getTipoRolloId())
                .orElseThrow(() -> new RuntimeException("Tipo de rollo no encontrado"));

        Rollo rollo = new Rollo();
        rollo.setNombre(dto.getNombre());
        rollo.setCantidad(dto.getCantidad());
        rollo.setPrecio(dto.getPrecio());
        rollo.setLargo(dto.getLargo());
         rollo.setMetrosDisponibles(dto.getLargo());// ← inicia con metros completos
        rollo.setAncho(dto.getAncho());
        rollo.setMarca(dto.getMarca()); // y en el save()
        rollo.setTipoRollo(tipoRollo);
        rollo.setProveedor(proveedor);

    int anio = LocalDate.now().getYear() % 100;
    int siguiente = rolloRepository.findMaxId().orElse(0L).intValue() + 1;
    rollo.setCodigo(String.format("ROLL-%02d-%03d", anio, siguiente));

    return toDTO(rolloRepository.save(rollo));
    }

    public void descontarMetros(Long id, BigDecimal metros) {
    Rollo rollo = rolloRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Rollo no encontrado"));

    if (rollo.getMetrosDisponibles() == null)
        rollo.setMetrosDisponibles(rollo.getLargo());

    BigDecimal restante = rollo.getMetrosDisponibles().subtract(metros);

    if (restante.compareTo(BigDecimal.ZERO) < 0)
        throw new RuntimeException(
            "Sin metros suficientes. Disponible: " + rollo.getMetrosDisponibles() + "m");

    rollo.setMetrosDisponibles(restante);
    rolloRepository.save(rollo);
    }

    public void delete(Long id) {
        rolloRepository.deleteById(id);
    }

    private RolloDTO toDTO(Rollo r) {
        return RolloDTO.builder()
                .id(r.getId())
                .nombre(r.getNombre())
                .cantidad(r.getCantidad())
                .precio(r.getPrecio())
                .largo(r.getLargo())
                .codigo(r.getCodigo())
                .metrosDisponibles(r.getMetrosDisponibles())
                .ancho(r.getAncho())
                .marca(r.getMarca())
                .tipoRolloId(r.getTipoRollo() != null ? r.getTipoRollo().getId() : null)
                .proveedorId(r.getProveedor() != null ? r.getProveedor().getId() : null)
                .build();
    }

    public RolloDTO agregarStock(Long id, Integer cantidad) {
    Rollo rollo = rolloRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rollo no encontrado"));

    if (cantidad == null || cantidad <= 0) {
        throw new RuntimeException("La cantidad debe ser mayor a 0");
    }

    Integer actual = rollo.getCantidad() != null ? rollo.getCantidad() : 0;
    rollo.setCantidad(actual + cantidad);

    return toDTO(rolloRepository.save(rollo));
}

public RolloReporteDTO getReporte(Long rolloId) {
    Rollo rollo = rolloRepository.findById(rolloId)
        .orElseThrow(() -> new RuntimeException("Rollo no encontrado"));

    List<OrdenProduccion> ordenes = ordenRepository.findByRolloId(rolloId);

    BigDecimal metrosUsados = ordenes.stream()
        .map(o -> o.getMetraje() != null ? o.getMetraje() : BigDecimal.ZERO)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal disponibles = rollo.getMetrosDisponibles() != null 
        ? rollo.getMetrosDisponibles() : rollo.getLargo();

    BigDecimal perdida = rollo.getLargo()
        .subtract(metrosUsados)
        .subtract(disponibles);

    List<RolloReporteDTO.UsoRolloDTO> usos = ordenes.stream()
        .map(o -> RolloReporteDTO.UsoRolloDTO.builder()
            .codigoOrden(o.getCodigoRecibo())
            .cliente(o.getCliente())
            .tipoTrabajo(o.getClass()
                .getAnnotation(jakarta.persistence.DiscriminatorValue.class)
                .value())
            .metrosUsados(o.getMetraje())
            .fecha(o.getFecha().toString())
            .build())
        .toList();

    return RolloReporteDTO.builder()
        .codigo(rollo.getCodigo())
        .nombre(rollo.getNombre())
        .largoTotal(rollo.getLargo())
        .metrosUsados(metrosUsados)
        .metrosDisponibles(disponibles)
        .perdida(perdida.max(BigDecimal.ZERO))
        .usos(usos)
        .build();
}
}