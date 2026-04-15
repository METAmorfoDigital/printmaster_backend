package com.bpm.printmaster.inventory.service;

import com.bpm.printmaster.inventory.dto.RolloDTO;
import com.bpm.printmaster.inventory.entity.Rollo;
import com.bpm.printmaster.inventory.entity.Proveedor;
import com.bpm.printmaster.maestras.entity.TablaMaestra;
import com.bpm.printmaster.inventory.repository.RolloRepository;
import com.bpm.printmaster.inventory.repository.ProveedorRepository;
import com.bpm.printmaster.maestras.repository.TablaMaestraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RolloService {

    private final RolloRepository rolloRepository;
    private final ProveedorRepository proveedorRepository;
    private final TablaMaestraRepository tablaMaestraRepository;

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
        rollo.setAncho(dto.getAncho());
        rollo.setMarca(dto.getMarca()); // y en el save()
        rollo.setTipoRollo(tipoRollo);
        rollo.setProveedor(proveedor);

        return toDTO(rolloRepository.save(rollo));
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
}