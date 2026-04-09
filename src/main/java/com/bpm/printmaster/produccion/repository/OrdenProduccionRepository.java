package com.bpm.printmaster.produccion.repository;


import com.bpm.printmaster.produccion.entity.OrdenProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdenProduccionRepository extends JpaRepository<OrdenProduccion, Long> {

    // ✔ Todas las órdenes por tipo (BIEN HECHO)
    @Query("""
    SELECT o FROM OrdenProduccion o
    LEFT JOIN FETCH o.rollo
    WHERE TYPE(o) = :tipo
    ORDER BY o.correlativo DESC
""")
List<OrdenProduccion> findByTipo(@Param("tipo") Class<?> tipo);

    // ✔ Último correlativo por año y tipo (YA LO TENÍAS BIEN)
    @Query("SELECT MAX(o.correlativo) FROM OrdenProduccion o WHERE o.anio = :anio AND TYPE(o) = :tipo")
    Optional<Integer> findMaxCorrelativoByAnioAndTipo(
        @Param("anio") Integer anio,
        @Param("tipo") Class<?> tipo
    );
}