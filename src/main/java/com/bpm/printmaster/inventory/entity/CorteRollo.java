package com.bpm.printmaster.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "corte_rollo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CorteRollo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rollo_id", nullable = false)
    private Rollo rollo;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(name = "item_index")
    private Integer itemIndex;

    // Metros cortados — misma unidad que metrosDisponibles
    @Column(name = "largo_cortado_m", nullable = false, precision = 10, scale = 3)
    private BigDecimal largoCortadoM;

    // Rectángulo dibujado (para trazabilidad visual)
    @Column(name = "x_m",     precision = 10, scale = 3) private BigDecimal xM;
@Column(name = "y_m",     precision = 10, scale = 3) private BigDecimal yM;
@Column(name = "ancho_m", precision = 10, scale = 3) private BigDecimal anchoM;
@Column(name = "alto_m",  precision = 10, scale = 3) private BigDecimal altoM;

    @Column(name = "fecha_corte", nullable = false)
    private LocalDateTime fechaCorte;

    @PrePersist
    void prePersist() { this.fechaCorte = LocalDateTime.now(); }
}