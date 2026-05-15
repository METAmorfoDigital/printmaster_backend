package com.bpm.printmaster.inventory.repository;
 
import com.bpm.printmaster.inventory.entity.SalidaMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
 
public interface SalidaMaterialRepository extends JpaRepository<SalidaMaterial, Long> {
 
    List<SalidaMaterial> findAllByOrderByFechaDesc();
 
    List<SalidaMaterial> findByTipoTrabajoOrderByFechaDesc(String tipoTrabajo);
 
    @Query("SELECT MAX(s.codigoSalida) FROM SalidaMaterial s WHERE s.codigoSalida LIKE CONCAT(:prefijo, '-%')")
    Optional<String> findMaxCodigo(String prefijo);
}