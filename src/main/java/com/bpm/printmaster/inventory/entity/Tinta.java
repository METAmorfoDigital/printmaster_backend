package com.bpm.printmaster.inventory.entity;

import com.bpm.printmaster.maestras.entity.TablaMaestra;

import jakarta.persistence.*;
import lombok.*;



@Entity
@DiscriminatorValue("TINTA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tinta extends Material {

    private String color;
    private String marca;

    // ✅ Relación a tabla maestra en lugar de enum
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_tinta_id")
    private TablaMaestra tipoTinta;
}
