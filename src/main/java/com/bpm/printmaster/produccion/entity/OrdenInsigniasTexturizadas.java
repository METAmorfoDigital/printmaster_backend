package com.bpm.printmaster.produccion.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("INSIGNIAS_T")
@Getter @Setter @NoArgsConstructor
public class OrdenInsigniasTexturizadas extends OrdenProduccion {

    private String observaciones;

    @OneToMany(
        mappedBy = "orden",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<DetalleInsignia> detalles = new ArrayList<>();

    // ── Helpers ──
    public void addDetalle(DetalleInsignia d) {
        detalles.add(d);
        d.setOrden(this);
    }

    public void clearDetalles() {
        detalles.forEach(d -> d.setOrden(null));
        detalles.clear();
    }

    // ── Override: total = suma de subtotales ──
    @Override
    protected BigDecimal calcularTotal() {
        return detalles.stream()
            .map(DetalleInsignia::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}