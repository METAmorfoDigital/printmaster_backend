package com.bpm.printmaster.produccion.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagos_orden")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PagoOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenProduccion orden;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(name = "tipo_pago")
    private String tipoPago;

    private String banco;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    private String nota;
}