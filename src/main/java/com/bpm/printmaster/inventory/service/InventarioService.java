package com.bpm.printmaster.inventory.service;

import com.bpm.printmaster.inventory.entity.Material;
import com.bpm.printmaster.inventory.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final MaterialRepository materialRepository;

    public List<Material> listarTodos() {
        return materialRepository.findAll();
    }

    public Material guardar(Material material) {
        return materialRepository.save(material);
    }

    public Material obtenerPorId(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));
    }

    public void eliminar(Long id) {
        materialRepository.deleteById(id);
    }
}