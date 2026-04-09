package com.bpm.printmaster.inventory.controller;

import com.bpm.printmaster.inventory.entity.Material;
import com.bpm.printmaster.inventory.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public List<Material> listarTodos() {
        return inventarioService.listarTodos();
    }

    @PostMapping
    public Material crear(@RequestBody Material material) {
        return inventarioService.guardar(material);
    }

    @GetMapping("/{id}")
    public Material obtener(@PathVariable Long id) {
        return inventarioService.obtenerPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
    }
}