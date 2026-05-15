package com.bpm.printmaster.inventory.repository;

import com.bpm.printmaster.inventory.entity.Rollo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RolloRepository extends JpaRepository<Rollo, Long> {

    // ── Existente ─────────────────────────────────────────────────
    @Query("SELECT MAX(r.id) FROM Rollo r")
    Optional<Long> findMaxId();

    // ── Nuevos ────────────────────────────────────────────────────

    // Todos los rollos de un tipo de trabajo
    List<Rollo> findByTipoTrabajo(String tipoTrabajo);

    @Query("SELECT MAX(r.codigo) FROM Rollo r WHERE r.codigo LIKE CONCAT(:prefijo, '-%')")
    Optional<String> findMaxCodigoByPrefijo(@Param("prefijo") String prefijo);
    
    @Query("SELECT MAX(r.numero) FROM Rollo r WHERE r.tipoTrabajo = :tipo")
    Optional<Integer> findMaxNumeroByTipo(@Param("tipo") String tipo);

    // Solo los que tienen stock disponible (metrosDisponibles > 0)
    List<Rollo> findByTipoTrabajoAndMetrosDisponiblesGreaterThan(
        String tipoTrabajo, BigDecimal minimo);

}