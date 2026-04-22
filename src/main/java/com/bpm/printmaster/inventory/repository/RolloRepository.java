package com.bpm.printmaster.inventory.repository;

import com.bpm.printmaster.inventory.entity.Rollo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RolloRepository extends JpaRepository<Rollo, Long> {
    
    @Query("SELECT MAX(r.id) FROM Rollo r")
    Optional<Long> findMaxId();
}