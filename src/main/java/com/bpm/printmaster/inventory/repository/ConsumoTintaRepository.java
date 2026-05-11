package com.bpm.printmaster.inventory.repository;

import com.bpm.printmaster.inventory.entity.ConsumoTinta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ConsumoTintaRepository extends JpaRepository<ConsumoTinta, Long> {
    List<ConsumoTinta> findAllByOrderByFechaDesc();
    List<ConsumoTinta> findByTipoTrabajoOrderByFechaDesc(String tipoTrabajo);
    List<ConsumoTinta> findByTinta_IdOrderByFechaDesc(Long tintaId);

    // ← reemplaza findByTinta_IdOrderByFechaDesc con este fetch join
    @Query("SELECT c FROM ConsumoTinta c JOIN FETCH c.tinta WHERE c.tinta.id = :tintaId ORDER BY c.fecha DESC")
    List<ConsumoTinta> findByTintaIdFetch(@Param("tintaId") Long tintaId);

    @Query("SELECT c FROM ConsumoTinta c JOIN FETCH c.tinta WHERE c.tipoTrabajo = :tipoTrabajo ORDER BY c.fecha DESC")
    List<ConsumoTinta> findByTipoTrabajoFetch(@Param("tipoTrabajo") String tipoTrabajo);

    @Query("SELECT c FROM ConsumoTinta c JOIN FETCH c.tinta ORDER BY c.fecha DESC")
    List<ConsumoTinta> findAllFetch();
}