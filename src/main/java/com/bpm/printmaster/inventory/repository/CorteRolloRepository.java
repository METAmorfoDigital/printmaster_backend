package com.bpm.printmaster.inventory.repository;

import com.bpm.printmaster.inventory.entity.CorteRollo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorteRolloRepository extends JpaRepository<CorteRollo, Long> {

    // Todos los cortes de una orden (para trazabilidad)
    List<CorteRollo> findByOrdenId(Long ordenId);

    // Todos los cortes de un rollo específico
    List<CorteRollo> findByRolloId(Long rolloId);
}