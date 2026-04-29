package com.bpm.printmaster.produccion.repository;

import com.bpm.printmaster.produccion.entity.OrdenProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdenProduccionRepository extends JpaRepository<OrdenProduccion, Long> {

    // ── Para DTF / DTF+ / Sublimado — trae el rollo en el mismo query ──
    @Query("""
        SELECT o FROM OrdenProduccion o
        LEFT JOIN FETCH o.rollo
        WHERE TYPE(o) = :tipo
        ORDER BY o.correlativo DESC
    """)
    List<OrdenProduccion> findByTipoConRollo(@Param("tipo") Class<?> tipo);

    // ── Para Insignias — trae los detalles en el mismo query ──
    // Sin LEFT JOIN FETCH o.rollo porque insignias no tiene rollo
@Query("""
    SELECT DISTINCT o FROM OrdenProduccion o
    LEFT JOIN FETCH o.detalles d
    WHERE TYPE(o) = :tipo
""")
List<OrdenProduccion> findByTipoConDetalles(@Param("tipo") Class<?> tipo);

    // ── Correlativo máximo por año y tipo ──
    @Query("""
        SELECT MAX(o.correlativo)
        FROM OrdenProduccion o
        WHERE o.anio = :anio AND TYPE(o) = :tipo
    """)
    Optional<Integer> findMaxCorrelativoByAnioAndTipo(
        @Param("anio") Integer anio,
        @Param("tipo") Class<?> tipo
    );

    // ── Órdenes por rollo — solo aplica a tipos con metraje ──
    @Query("""
        SELECT o FROM OrdenProduccion o
        LEFT JOIN FETCH o.rollo
        WHERE o.rollo.id = :rolloId
        ORDER BY o.fecha DESC
    """)
    List<OrdenProduccion> findByRolloId(@Param("rolloId") Long rolloId);
}