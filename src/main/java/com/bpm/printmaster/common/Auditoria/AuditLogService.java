package com.bpm.printmaster.common.Auditoria;



import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository repository;

    public void log(String accion, String entidad, String entidadId, String detalle) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = auth != null ? auth.getName() : "sistema";

        repository.save(AuditLog.builder()
            .usuario(usuario)
            .accion(accion)
            .entidad(entidad)
            .entidadId(entidadId)
            .detalle(detalle)
            .fecha(LocalDateTime.now())
            .build());
    }

    public Page<AuditLog> listar(Pageable pageable) {
        return repository.findAllByOrderByFechaDesc(pageable);
    }

    public Page<AuditLog> listarPorEntidad(String entidad, Pageable pageable) {
        return repository.findByEntidadOrderByFechaDesc(entidad, pageable);
    }

    public Page<AuditLog> listarPorUsuario(String usuario, Pageable pageable) {
        return repository.findByUsuarioOrderByFechaDesc(usuario, pageable);
    }
}
