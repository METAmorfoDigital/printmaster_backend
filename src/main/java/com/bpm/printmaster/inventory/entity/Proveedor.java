package com.bpm.printmaster.inventory.entity;

import com.bpm.printmaster.common.Auditable;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String telefono;

    @Column(unique = true, nullable = true)
    private String email;

    private String direccion;

    @Builder.Default  // ✅
    private boolean activo = true;
}