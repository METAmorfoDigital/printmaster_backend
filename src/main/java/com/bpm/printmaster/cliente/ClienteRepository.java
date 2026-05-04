package com.bpm.printmaster.cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
 
    // Búsqueda por nombre (case-insensitive, coincidencia parcial)
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
}
 