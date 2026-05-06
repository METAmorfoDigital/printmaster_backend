package com.bpm.printmaster.produccion.repository;

import com.bpm.printmaster.produccion.entity.QrCobrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface QrCobradorRepository extends JpaRepository<QrCobrador, Long> {

    List<QrCobrador> findByCobradorId(Long cobradorId);

    @Query("SELECT q FROM QrCobrador q WHERE q.fechaExpiracion <= :limite")
    List<QrCobrador> findPorExpirar(@Param("limite") LocalDate limite);
}