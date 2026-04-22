package com.bpm.printmaster.inventory.service;

import com.bpm.printmaster.inventory.dto.TintaDTO;
import com.bpm.printmaster.inventory.entity.Tinta;
import com.bpm.printmaster.inventory.entity.Proveedor;
import com.bpm.printmaster.maestras.entity.TablaMaestra;
import com.bpm.printmaster.inventory.repository.TintaRepository;
import com.bpm.printmaster.inventory.repository.ProveedorRepository;
import com.bpm.printmaster.maestras.repository.TablaMaestraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TintaService {

    private final TintaRepository tintaRepository;
    private final ProveedorRepository proveedorRepository;
    private final TablaMaestraRepository tablaMaestraRepository;

    public List<TintaDTO> getAll() {
        return tintaRepository.findAll()
                .stream().map(this::toDTO).toList();
    }

    public TintaDTO save(TintaDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        TablaMaestra tipoTinta = tablaMaestraRepository.findById(dto.getTipoTintaId())
                .orElseThrow(() -> new RuntimeException("Tipo de tinta no encontrado"));

        Tinta tinta = new Tinta();
        tinta.setNombre(dto.getNombre());
        tinta.setPrecio(dto.getPrecio());
        tinta.setCantidad(dto.getCantidad());
        tinta.setColor(dto.getColor());
        tinta.setMarca(dto.getMarca());
        tinta.setTipoTinta(tipoTinta);
        tinta.setProveedor(proveedor);

        return toDTO(tintaRepository.save(tinta));
    }

    public void delete(Long id) {
        tintaRepository.deleteById(id);
    }

    public TintaDTO update(Long id, TintaDTO dto) {
    Tinta tinta = tintaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Tinta no encontrada"));

    if (dto.getPrecio() != null)
        tinta.setPrecio(dto.getPrecio());

    if (dto.getCantidadAgregar() != null && dto.getCantidadAgregar() > 0)
        tinta.setCantidad(tinta.getCantidad() + dto.getCantidadAgregar());

    return toDTO(tintaRepository.save(tinta));
}

    public TintaDTO descontarStock(Long id) {
        Tinta tinta = tintaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tinta no encontrada"));

        if (tinta.getCantidad() <= 0)
            throw new RuntimeException("Sin stock disponible");

        tinta.setCantidad(tinta.getCantidad() - 1);
        return toDTO(tintaRepository.save(tinta));
    }
    private TintaDTO toDTO(Tinta t) {
        return TintaDTO.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .precio(t.getPrecio())
                .cantidad(t.getCantidad())
                .color(t.getColor())
                .marca(t.getMarca())
                .tipoTintaId(t.getTipoTinta() != null ? t.getTipoTinta().getId() : null)
                .proveedorId(t.getProveedor() != null ? t.getProveedor().getId() : null)
                .build();
    }
}