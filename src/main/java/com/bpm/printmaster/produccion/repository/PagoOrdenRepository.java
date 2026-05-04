package com.bpm.printmaster.produccion.repository;

import com.bpm.printmaster.produccion.entity.PagoOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface PagoOrdenRepository extends JpaRepository<PagoOrden, Long> {

    List<PagoOrden> findByOrdenIdOrderByFechaPagoAsc(Long ordenId);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoOrden p WHERE p.orden.id = :ordenId")
    BigDecimal sumMontoByOrdenId(@Param("ordenId") Long ordenId);
}