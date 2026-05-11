package com.bpm.printmaster.common.configuracion;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "configuracion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Configuracion {

    @Id
    @Column(nullable = false)
    private String clave;  // ej: "TIPO_CAMBIO_USD"

    @Column(nullable = false)
    private String valor;  // ej: "6.96"

    private String descripcion;
}