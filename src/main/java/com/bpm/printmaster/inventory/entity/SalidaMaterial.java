package com.bpm.printmaster.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "salida_material")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class SalidaMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_salida", unique = true)
    private String codigoSalida; // SAL-26-001

    @Column(nullable = false)
    private String tipoTrabajo;

    @Column(nullable = false)
    private String rolloNombre;

    @Column(nullable = false)
    private String rolloCodigo;

    @Column(nullable = false)
    private String rolloNumero;

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    private String nota;

    @OneToMany(mappedBy = "salida", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SalidaDetalle> detalles;
}