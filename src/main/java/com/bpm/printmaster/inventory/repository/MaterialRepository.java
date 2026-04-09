package com.bpm.printmaster.inventory.repository;

import com.bpm.printmaster.inventory.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}