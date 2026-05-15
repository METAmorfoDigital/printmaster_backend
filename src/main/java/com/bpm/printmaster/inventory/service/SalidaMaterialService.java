package com.bpm.printmaster.inventory.service;
 
import com.bpm.printmaster.inventory.dto.SalidaMaterialDTO;
import com.bpm.printmaster.inventory.entity.*;
import com.bpm.printmaster.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
@Service
@RequiredArgsConstructor
public class SalidaMaterialService {
 
    private final SalidaMaterialRepository salidaRepository;
    private final RolloRepository rolloRepository;
    private final TintaRepository tintaRepository;
    private final InsumoRepository insumoRepository;
    private final SalidaDetalleRepository salidaDetalleRepository; 
    @Transactional
    public SalidaMaterialDTO registrar(SalidaMaterialDTO dto) {
 
        // 1. Rollo activo obligatorio
        Rollo rollo = rolloRepository
            .findByTipoTrabajoAndMetrosDisponiblesGreaterThan(
                dto.getTipoTrabajo(), BigDecimal.ZERO)
            .stream().findFirst()
            .orElseThrow(() -> new RuntimeException(
                "No hay rollo activo para " + dto.getTipoTrabajo()));
 
        // 2. Validar y descontar stock de cada material
        for (SalidaMaterialDTO.DetalleDTO detalle : dto.getDetalles()) {
            switch (detalle.getMaterialTipo()) {
                case "TINTA" -> {
                    Tinta tinta = tintaRepository.findById(detalle.getMaterialId())
                        .orElseThrow(() -> new RuntimeException("Tinta no encontrada"));
                    int cant = detalle.getCantidad().intValue();
                    if (tinta.getCantidad() < cant)
                        throw new RuntimeException("Stock insuficiente de " + tinta.getNombre());
                    tinta.setCantidad(tinta.getCantidad() - cant);
                    tintaRepository.save(tinta);
                }
                case "POLIAMIDA", "BARNIZ" -> {
                    Insumo insumo = insumoRepository.findById(detalle.getMaterialId())
                        .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));
                    BigDecimal stockActual = BigDecimal.valueOf(insumo.getCantidad());
                    if (stockActual.compareTo(detalle.getCantidad()) < 0)
                        throw new RuntimeException("Stock insuficiente de " + insumo.getNombre());
                    insumo.setStockDecimal(stockActual.subtract(detalle.getCantidad()));
                    insumoRepository.save(insumo);
                }
            }
        }
 
        // 3. Generar código de salida
        String usuario = org.springframework.security.core.context
            .SecurityContextHolder.getContext()
            .getAuthentication().getName();
 
        int anio = LocalDate.now().getYear() % 100;
        String prefijo = String.format("SAL-%02d", anio);
        int siguiente = salidaRepository.findMaxCodigo(prefijo)
            .map(c -> Integer.parseInt(c.split("-")[2]) + 1)
            .orElse(1);
        String codigo = String.format("%s-%03d", prefijo, siguiente);
 
        // 4. Construir detalles
        List<SalidaDetalle> detalles = new ArrayList<>(dto.getDetalles().stream()
            .map(d -> SalidaDetalle.builder()
                .materialId(d.getMaterialId())
                .materialNombre(d.getMaterialNombre())
                .materialTipo(d.getMaterialTipo())
                .unidad(d.getUnidad())
                .cantidad(d.getCantidad())
                .build())
            .toList());
 
        // 5. Guardar salida
        SalidaMaterial salida = SalidaMaterial.builder()
            .codigoSalida(codigo)
            .tipoTrabajo(dto.getTipoTrabajo())
            .rolloNombre(rollo.getNombre())
            .rolloCodigo(rollo.getCodigo() != null ? rollo.getCodigo() : "S/C")
            .rolloNumero(rollo.getNumero() != null ? rollo.getNumero().toString() : "S/N")
            .usuario(usuario)
            .fecha(LocalDateTime.now())
            .nota(dto.getNota())
            .build();
 
       SalidaMaterial guardada = salidaRepository.save(salida);



List<SalidaDetalle> detallesGuardados = new ArrayList<>();
for (SalidaDetalle detalle : detalles) {
    detalle.setSalida(guardada);
    detallesGuardados.add(salidaDetalleRepository.save(detalle));
}

guardada.setDetalles(detallesGuardados); // ✅ asignar directamente
return toDTO(guardada);   
    }
 
    public List<SalidaMaterialDTO> getAll() {
        return salidaRepository.findAllByOrderByFechaDesc()
            .stream().map(this::toDTO).toList();
    }
 
    public List<SalidaMaterialDTO> getByTipoTrabajo(String tipo) {
        return salidaRepository.findByTipoTrabajoOrderByFechaDesc(tipo)
            .stream().map(this::toDTO).toList();
    }
 
    private SalidaMaterialDTO toDTO(SalidaMaterial s) {
        List<SalidaMaterialDTO.DetalleDTO> detalles = s.getDetalles() == null
            ? List.of()
            : s.getDetalles().stream()
                .map(d -> SalidaMaterialDTO.DetalleDTO.builder()
                    .materialId(d.getMaterialId())
                    .materialNombre(d.getMaterialNombre())
                    .materialTipo(d.getMaterialTipo())
                    .unidad(d.getUnidad())
                    .cantidad(d.getCantidad())
                    .build())
                .toList();
 
        return SalidaMaterialDTO.builder()
            .id(s.getId())
            .codigoSalida(s.getCodigoSalida())
            .tipoTrabajo(s.getTipoTrabajo())
            .rolloNombre(s.getRolloNombre())
            .rolloCodigo(s.getRolloCodigo())
            .rolloNumero(s.getRolloNumero())
            .usuario(s.getUsuario())
            .fecha(s.getFecha())
            .nota(s.getNota())
            .detalles(detalles)
            .build();
    }
}