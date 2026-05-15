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

    private String buildPrefijo(int anio, String tipoTrabajo) {
    String tipo = switch (tipoTrabajo.toUpperCase()) {
        case "DTF"         -> "DTF";
        case "DTF_PLUS"    -> "DTFP";
        case "SUBLIMADO"   -> "SUB";
        case "INSIGNIAS_T" -> "INS";
        default            -> "ROLL";
    };
    return String.format("%s-%02d", tipo, anio);
}

    // ← agregar este método privado
    private int siguienteNumero(int anio, String tipoTrabajo) {
        String prefijo = buildPrefijo(anio, tipoTrabajo);
        return rolloRepository.findMaxCodigoByPrefijo(prefijo)
            .map(codigo -> {
                String[] partes = codigo.split("-");
                return Integer.parseInt(partes[partes.length - 1]) + 1;
            })
            .orElse(1);
    }

    private int siguienteNumero(String tipoTrabajo, int fallback) {
        return rolloRepository.findMaxNumeroByTipo(tipoTrabajo)
            .map(n -> n + 1)
            .orElse(fallback); // si no hay rollos aún, arranca desde el del código
        }

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
        String prefijo = buildPrefijo(anio, dto.getTipoTrabajo());
        int siguiente = siguienteNumero(anio, dto.getTipoTrabajo());
        int siguienteNum = siguienteNumero(dto.getTipoTrabajo(), siguiente); 
        
        rollo.setCodigo(String.format("%s-%03d", prefijo, siguiente));
        rollo.setNumero(dto.getNumero() != null ? dto.getNumero() : siguienteNum); 

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
        String prefijo = buildPrefijo(anio, dto.getTipoTrabajo());

        int siguiente = siguienteNumero(anio, dto.getTipoTrabajo());
        int siguienteNum = siguienteNumero(dto.getTipoTrabajo(), siguiente);
        List<Rollo> rollos = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
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
            rollo.setCodigo(String.format("%s-%03d", prefijo, siguiente));
            rollo.setNumero(siguienteNum);
            siguiente++; 
            siguienteNum++;
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

    public List<RolloDTO> getByTipo(String tipoTrabajo) {
    return rolloRepository
        .findByTipoTrabajoAndMetrosDisponiblesGreaterThan(
            tipoTrabajo, BigDecimal.ZERO)
        .stream().map(this::toDTO).toList();
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
            .numero(rollo.getNumero() != null ? rollo.getNumero().toString() : null)
            .nombre(rollo.getNombre())
            .largoTotal(rollo.getLargo())
            .metrosUsados(metrosUsados)
            .metrosDisponibles(disponibles)
            .perdida(perdida.max(BigDecimal.ZERO))
            .usos(usos)
            .build();
    }

    public RolloDTO updateNumero(Long id, Integer nuevoNumero) {
    Rollo rollo = rolloRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Rollo no encontrado"));
    rollo.setNumero(nuevoNumero);
    return toDTO(rolloRepository.save(rollo));
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
            .numero(r.getNumero())
            .tipoTrabajo(r.getTipoTrabajo())
            .tipoRolloId(r.getTipoRollo() != null ? r.getTipoRollo().getId() : null)
            .proveedorId(r.getProveedor() != null ? r.getProveedor().getId() : null)
            .build();
    }

    public Integer getSiguienteNumero(String tipoTrabajo) {
    return rolloRepository.findMaxNumeroByTipo(tipoTrabajo)
        .map(n -> n + 1)
        .orElse(1);
}
}