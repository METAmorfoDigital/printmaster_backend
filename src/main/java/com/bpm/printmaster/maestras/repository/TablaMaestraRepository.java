package com.bpm.printmaster.maestras.repository;

import com.bpm.printmaster.maestras.entity.TablaMaestra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TablaMaestraRepository extends JpaRepository<TablaMaestra, Long> {
    // ✅ Buscar por categoría activos
    List<TablaMaestra> findByCategoriaAndActivoTrue(String categoria);
}