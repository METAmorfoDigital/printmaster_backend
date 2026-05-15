package com.bpm.printmaster.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "salida_detalle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SalidaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salida_id", nullable = false)
    private SalidaMaterial salida;

    @Column(nullable = false)
    private Long materialId;

    @Column(nullable = false)
    private String materialNombre;

    @Column(nullable = false)
    private String materialTipo; // TINTA, POLIAMIDA, BARNIZ

    @Column(nullable = false)
    private String unidad; // UNIDAD, KG, LITRO

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad;
}