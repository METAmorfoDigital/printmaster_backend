package com.bpm.printmaster.produccion.repository;

import com.bpm.printmaster.produccion.entity.OrdenProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdenProduccionRepository extends JpaRepository<OrdenProduccion, Long> {

    // ── DTF / DTF+ / Sublimado — rollo + cobrador + qr ──────────
    @Query("""
        SELECT o FROM OrdenProduccion o
        LEFT JOIN FETCH o.rollo
        LEFT JOIN FETCH o.cobrador
        LEFT JOIN FETCH o.qr
        WHERE TYPE(o) = :tipo
        ORDER BY o.correlativo DESC
    """)
    List<OrdenProduccion> findByTipoConRollo(@Param("tipo") Class<?> tipo);

    // ── Insignias — detalles + cobrador + qr ────────────────────
    @Query("""
        SELECT DISTINCT o FROM OrdenProduccion o
        LEFT JOIN FETCH o.detalles d
        LEFT JOIN FETCH o.cobrador
        LEFT JOIN FETCH o.qr
        WHERE TYPE(o) = :tipo
    """)
    List<OrdenProduccion> findByTipoConDetalles(@Param("tipo") Class<?> tipo);

    // ── Correlativo máximo por año y tipo ───────────────────────
    @Query("""
        SELECT MAX(o.correlativo)
        FROM OrdenProduccion o
        WHERE o.anio = :anio AND TYPE(o) = :tipo
    """)
    Optional<Integer> findMaxCorrelativoByAnioAndTipo(
        @Param("anio") Integer anio,
        @Param("tipo") Class<?> tipo
    );

    // ── Órdenes por rollo ────────────────────────────────────────
    @Query("""
        SELECT o FROM OrdenProduccion o
        LEFT JOIN FETCH o.rollo
        WHERE o.rollo.id = :rolloId
        ORDER BY o.fecha DESC
    """)
    List<OrdenProduccion> findByRolloId(@Param("rolloId") Long rolloId);
}