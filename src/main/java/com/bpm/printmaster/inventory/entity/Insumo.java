package com.bpm.printmaster.inventory.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;


@Entity
@DiscriminatorValue("INSUMO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Insumo extends Material {

    @Column(name = "unidad")
    @Enumerated(EnumType.STRING)
    private UnidadInsumo unidad; // KG, LITRO

    @Column(name = "tipo_insumo")
    @Enumerated(EnumType.STRING)
    private TipoInsumo tipoInsumo; // POLIAMIDA, BARNIZ

    private String marca;


    @Column(name = "stock_decimal", precision = 10, scale = 3)
    private BigDecimal stockDecimal;
    // cantidad y precio se heredan de Material
    // tipoTrabajo se hereda de Material
    // proveedor se hereda de Material
}