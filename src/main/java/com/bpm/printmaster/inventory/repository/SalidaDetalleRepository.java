package com.bpm.printmaster.inventory.repository;
 
import com.bpm.printmaster.inventory.entity.SalidaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface SalidaDetalleRepository extends JpaRepository<SalidaDetalle, Long> {
}