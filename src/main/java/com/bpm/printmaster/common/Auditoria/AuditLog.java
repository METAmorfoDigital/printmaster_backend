package com.bpm.printmaster.common.Auditoria;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String usuario;        // quien hizo la acción

    @Column(nullable = false)
    private String accion;         // CREATE, UPDATE, DELETE

    @Column(nullable = false)
    private String entidad;        // OrdenProduccion, PagoOrden, Rollo, etc.

    private String entidadId;      // id del registro afectado

    @Column(columnDefinition = "TEXT")
    private String detalle;        // descripción legible del cambio

    @Column(nullable = false)
    private LocalDateTime fecha;
}