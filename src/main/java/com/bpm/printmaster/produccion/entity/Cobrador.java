package com.bpm.printmaster.produccion.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "cobradores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Cobrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private boolean activo = true;

    @OneToMany(mappedBy = "cobrador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QrCobrador> qrs = new ArrayList<>();
}
