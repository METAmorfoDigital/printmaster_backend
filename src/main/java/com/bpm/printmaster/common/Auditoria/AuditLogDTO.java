package com.bpm.printmaster.common.Auditoria;


import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuditLogDTO {
    private Long id;
    private String usuario;
    private String accion;
    private String entidad;
    private String entidadId;
    private String detalle;
    private LocalDateTime fecha;
}
