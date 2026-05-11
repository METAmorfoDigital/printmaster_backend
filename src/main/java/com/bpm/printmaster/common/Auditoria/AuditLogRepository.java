package com.bpm.printmaster.common.Auditoria;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findAllByOrderByFechaDesc(Pageable pageable);
    Page<AuditLog> findByEntidadOrderByFechaDesc(String entidad, Pageable pageable);
    Page<AuditLog> findByUsuarioOrderByFechaDesc(String usuario, Pageable pageable);
}
