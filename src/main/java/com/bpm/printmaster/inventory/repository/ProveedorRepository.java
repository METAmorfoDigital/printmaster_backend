package com.bpm.printmaster.inventory.repository;

import com.bpm.printmaster.inventory.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findByActivoTrue();
}