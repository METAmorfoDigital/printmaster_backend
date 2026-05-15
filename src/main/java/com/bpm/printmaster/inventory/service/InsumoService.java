package com.bpm.printmaster.inventory.service;
 
import com.bpm.printmaster.inventory.dto.InsumoDTO;
import com.bpm.printmaster.inventory.entity.Insumo;
import com.bpm.printmaster.inventory.entity.TipoInsumo;
import com.bpm.printmaster.inventory.entity.UnidadInsumo;
import com.bpm.printmaster.inventory.entity.Proveedor;
import com.bpm.printmaster.inventory.repository.InsumoRepository;
import com.bpm.printmaster.inventory.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
 
@Service
@RequiredArgsConstructor
public class InsumoService {
 
    private final InsumoRepository insumoRepository;
    private final ProveedorRepository proveedorRepository;
 
    public List<InsumoDTO> getAll() {
        return insumoRepository.findAll().stream().map(this::toDTO).toList();
    }
 
    public InsumoDTO save(InsumoDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
 
        Insumo insumo = new Insumo();
        insumo.setNombre(dto.getNombre());
        insumo.setPrecio(dto.getPrecio());
        insumo.setStockDecimal(dto.getCantidad() != null
            ? dto.getCantidad() : java.math.BigDecimal.ZERO);
        insumo.setUnidad(UnidadInsumo.valueOf(dto.getUnidad()));
        insumo.setTipoInsumo(TipoInsumo.valueOf(dto.getTipoInsumo()));
        insumo.setMarca(dto.getMarca());
        insumo.setTipoTrabajo(dto.getTipoTrabajo());
        insumo.setProveedor(proveedor);
 
        return toDTO(insumoRepository.save(insumo));
    }
 
    public InsumoDTO update(Long id, InsumoDTO dto) {
        Insumo insumo = insumoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));
 
        if (dto.getPrecio() != null)
            insumo.setPrecio(dto.getPrecio());
 
        if (dto.getCantidad() != null && dto.getCantidad().compareTo(java.math.BigDecimal.ZERO) > 0)
            insumo.setStockDecimal(insumo.getStockDecimal().add(dto.getCantidad()));
 
        return toDTO(insumoRepository.save(insumo));
    }
 
    public void delete(Long id) {
        insumoRepository.deleteById(id);
    }
 
    // Descontar stock al registrar salida
    public void descontarStock(Long id, BigDecimal cantidad) {
        Insumo insumo = insumoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));
 
        BigDecimal stockActual = insumo.getStockDecimal() != null 
        ? insumo.getStockDecimal() : BigDecimal.ZERO;
    if (stockActual.compareTo(cantidad) < 0)
        throw new RuntimeException("Stock insuficiente de " + insumo.getNombre()
            + ". Disponible: " + stockActual + " " + insumo.getUnidad());
    insumo.setStockDecimal(stockActual.subtract(cantidad));
    insumoRepository.save(insumo);
    }
 
    private InsumoDTO toDTO(Insumo i) {
        return InsumoDTO.builder()
            .id(i.getId())
            .nombre(i.getNombre())
            .precio(i.getPrecio())
            .cantidad(i.getStockDecimal())
            .unidad(i.getUnidad() != null ? i.getUnidad().name() : null)
            .tipoInsumo(i.getTipoInsumo() != null ? i.getTipoInsumo().name() : null)
            .marca(i.getMarca())
            .tipoTrabajo(i.getTipoTrabajo())
            .proveedorId(i.getProveedor() != null ? i.getProveedor().getId() : null)
            .build();
    }
}