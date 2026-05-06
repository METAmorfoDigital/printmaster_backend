package com.bpm.printmaster.produccion.service;

import com.bpm.printmaster.produccion.dto.CobradorDTO;
import com.bpm.printmaster.produccion.dto.QrCobradorDTO;
import com.bpm.printmaster.produccion.entity.Cobrador;
import com.bpm.printmaster.produccion.entity.QrCobrador;
import com.bpm.printmaster.produccion.repository.CobradorRepository;
import com.bpm.printmaster.produccion.repository.QrCobradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CobradorService {

    private final CobradorRepository cobradorRepository;
    private final QrCobradorRepository qrRepository;

    private static final int DIAS_ALERTA = 7;

    // ── Cobradores ──

    @Transactional(readOnly = true)
    public List<CobradorDTO> listarActivos() {
        return cobradorRepository.findByActivoTrue()
            .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<CobradorDTO> listarTodos() {
        return cobradorRepository.findAll()
            .stream().map(this::toDTO).toList();
    }

    @Transactional
    public CobradorDTO crear(CobradorDTO dto) {
        Cobrador cobrador = new Cobrador();
        cobrador.setNombre(dto.getNombre());
        cobrador.setActivo(true);
        return toDTO(cobradorRepository.save(cobrador));
    }

    @Transactional
    public CobradorDTO actualizar(Long id, CobradorDTO dto) {
        Cobrador cobrador = cobradorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cobrador no encontrado"));
        cobrador.setNombre(dto.getNombre());
        cobrador.setActivo(dto.isActivo());
        return toDTO(cobradorRepository.save(cobrador));
    }

    @Transactional
    public void eliminar(Long id) {
        cobradorRepository.deleteById(id);
    }

    // ── QR ──

    @Transactional
    public QrCobradorDTO agregarQr(Long cobradorId, QrCobradorDTO dto) {
        Cobrador cobrador = cobradorRepository.findById(cobradorId)
            .orElseThrow(() -> new RuntimeException("Cobrador no encontrado"));

        QrCobrador qr = new QrCobrador();
        qr.setCobrador(cobrador);
        qr.setBanco(dto.getBanco());
        qr.setImagenBase64(dto.getImagenBase64());
        qr.setFechaExpiracion(dto.getFechaExpiracion());

        return toQrDTO(qrRepository.save(qr));
    }

    @Transactional
    public void eliminarQr(Long qrId) {
        qrRepository.deleteById(qrId);
    }

    // ── Alertas ──

    @Transactional(readOnly = true)
    public List<QrCobradorDTO> qrPorExpirar() {
        LocalDate limite = LocalDate.now().plusDays(DIAS_ALERTA);
        return qrRepository.findPorExpirar(limite)
            .stream().map(this::toQrDTO).toList();
    }

    // ── Mappers ──

    private CobradorDTO toDTO(Cobrador c) {
        return CobradorDTO.builder()
            .id(c.getId())
            .nombre(c.getNombre())
            .activo(c.isActivo())
            .qrs(c.getQrs().stream().map(this::toQrDTO).toList())
            .build();
    }

    private QrCobradorDTO toQrDTO(QrCobrador q) {
        LocalDate limite = LocalDate.now().plusDays(DIAS_ALERTA);
        boolean porExpirar = q.getFechaExpiracion() != null &&
                             !q.getFechaExpiracion().isAfter(limite);
        return QrCobradorDTO.builder()
            .id(q.getId())
            .cobradorId(q.getCobrador().getId())
            .banco(q.getBanco())
            .imagenBase64(q.getImagenBase64())
            .fechaExpiracion(q.getFechaExpiracion())
            .porExpirar(porExpirar)
            .build();
    }
}