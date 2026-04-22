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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rollo_id")
    private Rollo rollo;

    // --- Producción y Subtotales ---
    private BigDecimal metraje;
    private BigDecimal costoImpresion;
    private BigDecimal subtotalImpresion;

    private BigDecimal cantidadPlanchado;
    private BigDecimal costoPlanchado;
    private BigDecimal subtotalPlanchado;



    private BigDecimal costoDiseno;

    @Column(nullable = false)
    private BigDecimal total;

    // --- NUEVOS CAMPOS: Información de Pago ---
    
    @Column(name = "tipo_pago") // Ejemplo: EFECTIVO, TRANSFERENCIA, QR
    private String tipoPago;

    private String banco; // Ejemplo: BCP, BBVA, o null si es efectivo

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    // --- Lógica de Integridad ---
    @PrePersist
    @PreUpdate
    public void calcularTotales() {
        this.subtotalImpresion = safeMultiply(metraje, costoImpresion);
        this.subtotalPlanchado = safeMultiply(cantidadPlanchado, costoPlanchado);
        
        BigDecimal diseño = (costoDiseno != null) ? costoDiseno : BigDecimal.ZERO;

        this.total = subtotalImpresion.add(subtotalPlanchado)
                                      .add(diseño);
    }

    private BigDecimal safeMultiply(BigDecimal a, BigDecimal b) {
        return (a != null && b != null) ? a.multiply(b) : BigDecimal.ZERO;
    }

    public String getCodigoRecibo() {
        return correlativo + "/" + (anio % 100);
    }

    // Método extra para tus KPIs: ¿Está pagado?
    public boolean isPagado() {
        return fechaPago != null;
    }
}