package com.bpm.printmaster.produccion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("INSIGNIAS_T")
@Getter
@Setter
@NoArgsConstructor
public class OrdenInsigniasTexturizadas extends OrdenProduccion {
    // Campos específicos si se agregan en el futuro
}