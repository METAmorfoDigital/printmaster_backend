package com.bpm.printmaster.produccion.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "qr_cobrador")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class QrCobrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cobrador_id", nullable = false)
    private Cobrador cobrador;

    @Column(nullable = false)
    private String banco;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String imagenBase64;

    @Column(name = "fecha_expiracion")
    private LocalDate fechaExpiracion;
}