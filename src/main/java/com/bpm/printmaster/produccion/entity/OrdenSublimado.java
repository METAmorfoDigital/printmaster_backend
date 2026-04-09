package com.bpm.printmaster.produccion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("SUBLIMADO")
@Getter
@Setter
@NoArgsConstructor


public class OrdenSublimado extends OrdenProduccion {
    // Campos específicos de Sublimado si se agregan en el futuro
}