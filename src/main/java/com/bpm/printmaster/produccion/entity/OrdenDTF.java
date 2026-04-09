package com.bpm.printmaster.produccion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("DTF")
@Getter
@Setter
@NoArgsConstructor


public class OrdenDTF extends OrdenProduccion {
    // Campos específicos de DTF si se agregan en el futuro
}