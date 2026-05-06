package com.bpm.printmaster.produccion.repository;

import com.bpm.printmaster.produccion.entity.Cobrador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CobradorRepository extends JpaRepository<Cobrador, Long> {
    List<Cobrador> findByActivoTrue();
}