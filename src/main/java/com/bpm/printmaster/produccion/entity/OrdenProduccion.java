package com.bpm.printmaster.produccion.entity;

import com.bpm.printmaster.common.Auditable;
import com.bpm.printmaster.inventory.entity.Rollo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_trabajo", discriminatorType = DiscriminatorType.STRING)
@Table(name = "ordenes_produccion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public abstract class OrdenProduccion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer correlativo;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private String cliente;

    @Column(nullable = false)
    private LocalDate fecha;

    private LocalDate fechaEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rollo_id")
    private Rollo rollo;

    // ── Cobrador y QR ────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cobrador_id")
    private Cobrador cobrador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_id")
    private QrCobrador qr;
    // ─────────────────────────────────────────────────────────────

    private BigDecimal metraje;
    private BigDecimal costoImpresion;
    private BigDecimal subtotalImpresion;

    private BigDecimal cantidadPlanchado;
    private BigDecimal costoPlanchado;
    private BigDecimal subtotalPlanchado;

    private BigDecimal costoDiseno;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "tipo_pago")
    private String tipoPago;

    private String banco;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @PrePersist
    @PreUpdate
    public final void calcularTotales() {
        this.total = calcularTotal();
    }

    protected BigDecimal calcularTotal() {
        this.subtotalImpresion = safeMultiply(metraje, costoImpresion);
        this.subtotalPlanchado = safeMultiply(cantidadPlanchado, costoPlanchado);
        BigDecimal diseno = costoDiseno != null ? costoDiseno : BigDecimal.ZERO;
        return subtotalImpresion.add(subtotalPlanchado).add(diseno);
    }

    protected BigDecimal safeMultiply(BigDecimal a, BigDecimal b) {
        return (a != null && b != null) ? a.multiply(b) : BigDecimal.ZERO;
    }

    public String getCodigoRecibo() {
        return correlativo + "/" + (anio % 100);
    }

    @Transient
    private String estadoPago = "PENDIENTE";

    @Transient
    private java.math.BigDecimal sumaPagos = java.math.BigDecimal.ZERO;

    public boolean isPagado() {
        return "PAGADO".equals(estadoPago);
    }
}