package com.bpm.printmaster.maestras.entity;

import com.bpm.printmaster.common.Auditable;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tablas_maestras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TablaMaestra extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private String valor;

    private String descripcion;

    @Builder.Default  // ✅ fix — respeta el valor por defecto
    private boolean activo = true;
}