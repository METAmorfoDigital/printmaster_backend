package com.bpm.printmaster.inventory.repository;
 
import com.bpm.printmaster.inventory.entity.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    List<Insumo> findByTipoTrabajo(String tipoTrabajo);
    List<Insumo> findByTipoInsumo(String tipoInsumo);
}