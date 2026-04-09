package com.bpm.printmaster.maestras.service;

import com.bpm.printmaster.maestras.dto.TablaMaestraDTO;
import com.bpm.printmaster.maestras.entity.TablaMaestra;
import com.bpm.printmaster.maestras.repository.TablaMaestraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TablaMaestraService {

    private final TablaMaestraRepository repository;

    // ✅ Obtener opciones por categoría para el select
    public List<TablaMaestraDTO> getPorCategoria(String categoria) {
        return repository.findByCategoriaAndActivoTrue(categoria)
                .stream().map(this::toDTO).toList();
    }

    // ✅ Agregar nuevo valor al select
    public TablaMaestraDTO save(TablaMaestraDTO dto) {
        // Verificar duplicado
        boolean existe = repository
            .findByCategoriaAndActivoTrue(dto.getCategoria())
            .stream()
            .anyMatch(t -> t.getValor()
                .equalsIgnoreCase(dto.getValor()));

        if (existe) {
            throw new RuntimeException(
                "Ya existe '" + dto.getValor() + 
                "' en " + dto.getCategoria());
        }

        TablaMaestra tabla = TablaMaestra.builder()
                .categoria(dto.getCategoria())
                .valor(dto.getValor())
                .descripcion(dto.getDescripcion())
                .activo(true)
                .build();

        return toDTO(repository.save(tabla));
    }

    // ✅ Soft delete
    public void delete(Long id) {
        TablaMaestra t = repository.findById(id)
                .orElseThrow(() -> 
                    new RuntimeException("Registro no encontrado"));
        t.setActivo(false);
        repository.save(t);
    }

    private TablaMaestraDTO toDTO(TablaMaestra t) {
        return TablaMaestraDTO.builder()
                .id(t.getId())
                .categoria(t.getCategoria())
                .valor(t.getValor())
                .descripcion(t.getDescripcion())
                .activo(t.isActivo())
                .build();
    }
}