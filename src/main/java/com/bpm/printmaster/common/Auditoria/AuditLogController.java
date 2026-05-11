package com.bpm.printmaster.common.Auditoria;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogDTO>> listar(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) String usuario) {

        PageRequest pageable = PageRequest.of(page, size);

        Page<AuditLog> resultado = entidad != null
            ? auditLogService.listarPorEntidad(entidad, pageable)
            : usuario != null
                ? auditLogService.listarPorUsuario(usuario, pageable)
                : auditLogService.listar(pageable);

        return ResponseEntity.ok(resultado.map(a -> AuditLogDTO.builder()
            .id(a.getId())
            .usuario(a.getUsuario())
            .accion(a.getAccion())
            .entidad(a.getEntidad())
            .entidadId(a.getEntidadId())
            .detalle(a.getDetalle())
            .fecha(a.getFecha())
            .build()));
    }
}