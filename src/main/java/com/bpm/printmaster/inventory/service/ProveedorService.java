package com.bpm.printmaster.inventory.service;

import com.bpm.printmaster.inventory.dto.ProveedorDTO;
import com.bpm.printmaster.inventory.entity.Proveedor;
import com.bpm.printmaster.inventory.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);
    private final ProveedorRepository proveedorRepository;

    public List<ProveedorDTO> getAll() {
        return proveedorRepository.findByActivoTrue()
                .stream().map(this::toDTO).toList();
    }

    public ProveedorDTO save(ProveedorDTO dto) {
        boolean existe = proveedorRepository.findByActivoTrue()
                .stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(dto.getNombre()));

        if (existe) {
            throw new RuntimeException("Ya existe un proveedor con el nombre: "
                    + dto.getNombre());
        }

        // ✅ Convierte email vacío a null para evitar conflicto unique
        String email = (dto.getEmail() != null && dto.getEmail().isBlank()) 
            ? null 
            : dto.getEmail();

        Proveedor proveedor = Proveedor.builder()
                .nombre(dto.getNombre())
                .telefono(dto.getTelefono())
                .email(email) // ← usa email limpio
                .direccion(dto.getDireccion())
                .activo(true)
                .build();

        Proveedor saved = proveedorRepository.save(proveedor);
        logger.info("Proveedor creado: {}", saved.getNombre());
        return toDTO(saved);
    }

    public ProveedorDTO update(Long id, ProveedorDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        proveedor.setDireccion(dto.getDireccion());

        return toDTO(proveedorRepository.save(proveedor));
    }

    // ✅ Soft delete
    public void delete(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
        logger.info("Proveedor desactivado: {}", proveedor.getNombre());
    }

    private ProveedorDTO toDTO(Proveedor p) {
        return ProveedorDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .telefono(p.getTelefono())
                .email(p.getEmail())
                .direccion(p.getDireccion())
                .activo(p.isActivo())
                .build();
    }
}