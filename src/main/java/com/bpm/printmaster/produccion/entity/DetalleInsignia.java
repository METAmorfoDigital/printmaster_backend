package com.bpm.printmaster.produccion.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_insignia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DetalleInsignia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenInsigniasTexturizadas orden;

    @Column(nullable = false)
    private String descripcion;        // "Logo Barcelona"

    @Column(nullable = false)
    private String tamano;             // "2x7.2"

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Transient
    public BigDecimal getSubtotal() {
        if (cantidad == null || precioUnitario == null) return BigDecimal.ZERO;
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}