package com.bpm.printmaster.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import com.bpm.printmaster.common.Auditable;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_material", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Material extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private BigDecimal precio;
    
    private Integer cantidad;

    @Column(name = "tipo_trabajo", length = 20)
    private String tipoTrabajo;

    // ✅ Relación con Proveedor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;
}