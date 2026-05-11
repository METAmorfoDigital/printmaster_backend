package com.bpm.printmaster.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consumo_tinta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConsumoTinta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tinta_id", nullable = false)
    private Tinta tinta;

    @Column(nullable = false)
    private String tipoTrabajo;    // DTF, DTF_PLUS, SUBLIMADO

    @Column(nullable = false)
    private String rolloNombre;    // nombre del rollo activo

    @Column(nullable = false)
    private String rolloCodigo;    // código del rollo activo

    @Column(nullable = false)
    private String usuario;        // quien descontó

    @Column(nullable = false)
    private LocalDateTime fecha;

    private String nota;
}