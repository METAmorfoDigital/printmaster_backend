package com.bpm.printmaster.inventory.entity;

import com.bpm.printmaster.maestras.entity.TablaMaestra;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("ROLLO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rollo extends Material {

    private BigDecimal largo;  // ✅ BigDecimal para metros
    private BigDecimal ancho;  // ✅ BigDecimal para metros
    private String marca; // agregar al RolloDTO.java

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_rollo_id")
    private TablaMaestra tipoRollo;
}