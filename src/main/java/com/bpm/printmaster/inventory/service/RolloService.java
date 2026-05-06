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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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

    // ── Guardar uno solo (mantener por compatibilidad) ──
    public RolloDTO save(RolloDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        TablaMaestra tipoRollo = tablaMaestraRepository.findById(dto.getTipoRolloId())
                .orElseThrow(() -> new RuntimeException("Tipo de rollo no encontrado"));

        Rollo rollo = new Rollo();
        rollo.setNombre(dto.getNombre());
        rollo.setPrecio(dto.getPrecio());
        rollo.setLargo(dto.getLargo());
        rollo.setMetrosDisponibles(dto.getLargo());
        rollo.setAncho(dto.getAncho());
        rollo.setMarca(dto.getMarca());
        rollo.setTipoTrabajo(dto.getTipoTrabajo());
        rollo.setTipoRollo(tipoRollo);
        rollo.setProveedor(proveedor);

        int anio = LocalDate.now().getYear() % 100;
        int siguiente = rolloRepository.findMaxId().orElse(0L).intValue() + 1;
        rollo.setCodigo(String.format("ROLL-%02d-%03d", anio, siguiente));

        return toDTO(rolloRepository.save(rollo));
    }

    // ── Guardar en lote ──
    @Transactional
    public List<RolloDTO> saveLote(RolloDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        TablaMaestra tipoRollo = tablaMaestraRepository.findById(dto.getTipoRolloId())
            .orElseThrow(() -> new RuntimeException("Tipo de rollo no encontrado"));

        int cantidad = dto.getCantidadLote() != null && dto.getCantidadLote() > 0
            ? dto.getCantidadLote() : 1;

        int anio = LocalDate.now().getYear() % 100;
        long siguiente = rolloRepository.findMaxId().orElse(0L);

        List<Rollo> rollos = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            siguiente++;
            Rollo rollo = new Rollo();
            rollo.setNombre(dto.getNombre());
            rollo.setPrecio(dto.getPrecio());
            rollo.setLargo(dto.getLargo());
            rollo.setMetrosDisponibles(dto.getLargo());
            rollo.setAncho(dto.getAncho());
            rollo.setMarca(dto.getMarca());
            rollo.setTipoTrabajo(dto.getTipoTrabajo());
            rollo.setTipoRollo(tipoRollo);
            rollo.setProveedor(proveedor);
            rollo.setCodigo(String.format("ROLL-%02d-%03d", anio, siguiente));
            rollos.add(rollo);
        }

        return rolloRepository.saveAll(rollos)
            .stream().map(this::toDTO).toList();
    }

    // ── Descontar metros ──
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

    // ── Agregar stock ──
    public RolloDTO agregarStock(Long id, Integer cantidad) {
        Rollo rollo = rolloRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rollo no encontrado"));

        if (cantidad == null || cantidad <= 0)
            throw new RuntimeException("La cantidad debe ser mayor a 0");

        // Agregar metros al rollo existente
        BigDecimal metrosActuales = rollo.getMetrosDisponibles() != null
            ? rollo.getMetrosDisponibles() : BigDecimal.ZERO;
        rollo.setMetrosDisponibles(metrosActuales.add(rollo.getLargo()));

        return toDTO(rolloRepository.save(rollo));
    }

    // ── Eliminar ──
    public void delete(Long id) {
        rolloRepository.deleteById(id);
    }

    // ── Reporte ──
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

    // ── Mapper ──
    private RolloDTO toDTO(Rollo r) {
        return RolloDTO.builder()
            .id(r.getId())
            .nombre(r.getNombre())
            .precio(r.getPrecio())
            .largo(r.getLargo())
            .codigo(r.getCodigo())
            .metrosDisponibles(r.getMetrosDisponibles())
            .ancho(r.getAncho())
            .marca(r.getMarca())
            .tipoTrabajo(r.getTipoTrabajo())
            .tipoRolloId(r.getTipoRollo() != null ? r.getTipoRollo().getId() : null)
            .proveedorId(r.getProveedor() != null ? r.getProveedor().getId() : null)
            .build();
    }
}