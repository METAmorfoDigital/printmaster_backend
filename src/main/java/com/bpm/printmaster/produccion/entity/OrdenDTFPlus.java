package com.bpm.printmaster.produccion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("DTF_PLUS")
@Getter
@Setter
@NoArgsConstructor


public class OrdenDTFPlus extends OrdenProduccion {
    // Campos específicos de DTF Plus si se agregan en el futuro
}