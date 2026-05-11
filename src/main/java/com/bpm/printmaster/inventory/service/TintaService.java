package com.bpm.printmaster.inventory.service;

import com.bpm.printmaster.inventory.dto.ConsumoTintaDTO;
import com.bpm.printmaster.inventory.dto.TintaDTO;
import com.bpm.printmaster.inventory.entity.Tinta;
import com.bpm.printmaster.inventory.entity.ConsumoTinta;
import com.bpm.printmaster.inventory.entity.Proveedor;
import com.bpm.printmaster.inventory.entity.Rollo;
import com.bpm.printmaster.maestras.entity.TablaMaestra;
import com.bpm.printmaster.inventory.repository.TintaRepository;
import com.bpm.printmaster.inventory.repository.ConsumoTintaRepository;
import com.bpm.printmaster.inventory.repository.ProveedorRepository;
import com.bpm.printmaster.inventory.repository.RolloRepository;
import com.bpm.printmaster.maestras.repository.TablaMaestraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TintaService {

    private final TintaRepository tintaRepository;
    private final ProveedorRepository proveedorRepository;
    private final TablaMaestraRepository tablaMaestraRepository;
    private final ConsumoTintaRepository consumoRepository;
    private final RolloRepository rolloRepository;

    public List<TintaDTO> getAll() {
        return tintaRepository.findAll()
                .stream().map(this::toDTO).toList();

    }

    public TintaDTO save(TintaDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        TablaMaestra tipoTinta = tablaMaestraRepository.findById(dto.getTipoTintaId())
                .orElseThrow(() -> new RuntimeException("Tipo de tinta no encontrado"));

        Tinta tinta = new Tinta();
        tinta.setNombre(dto.getNombre());
        tinta.setPrecio(dto.getPrecio());
        tinta.setCantidad(dto.getCantidad());
        tinta.setColor(dto.getColor());
        tinta.setMarca(dto.getMarca());
        tinta.setTipoTinta(tipoTinta);
        tinta.setProveedor(proveedor);

        return toDTO(tintaRepository.save(tinta));
    }

    public void delete(Long id) {
        if (!tintaRepository.existsById(id))
            throw new RuntimeException("Tinta no encontrada con id: " + id);
        tintaRepository.deleteById(id);
    }

    public TintaDTO update(Long id, TintaDTO dto) {
    Tinta tinta = tintaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Tinta no encontrada"));

    if (dto.getPrecio() != null)
        tinta.setPrecio(dto.getPrecio());

    if (dto.getCantidadAgregar() != null && dto.getCantidadAgregar() > 0)
        tinta.setCantidad(tinta.getCantidad() + dto.getCantidadAgregar());

    return toDTO(tintaRepository.save(tinta));
}

    @Transactional
    public TintaDTO descontarStock(Long id, String tipoTrabajo, String nota) {
        Tinta tinta = tintaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tinta no encontrada"));

        if (tinta.getCantidad() <= 0)
            throw new RuntimeException("Sin stock disponible");

        // buscar rollo activo del tipo de trabajo
        Rollo rollo = rolloRepository
            .findByTipoTrabajoAndMetrosDisponiblesGreaterThan(
                tipoTrabajo, java.math.BigDecimal.ZERO)
            .stream().findFirst()
            .orElse(null);

        // obtener usuario del contexto
        String usuario = org.springframework.security.core.context
            .SecurityContextHolder.getContext()
            .getAuthentication().getName();

        // registrar consumo
        ConsumoTinta consumo = ConsumoTinta.builder()
            .tinta(tinta)
            .tipoTrabajo(tipoTrabajo)
            .rolloNombre(rollo != null ? rollo.getNombre() : "Sin rollo")
            .rolloCodigo(rollo != null ? rollo.getCodigo() : "-")
            .usuario(usuario)
            .fecha(java.time.LocalDateTime.now())
            .nota(nota)
            .build();
        consumoRepository.save(consumo);

        // descontar stock
        tinta.setCantidad(tinta.getCantidad() - 1);
        return toDTO(tintaRepository.save(tinta));
    }

    public List<ConsumoTintaDTO> getConsumos(String tipoTrabajo, Long tintaId) {
    List<ConsumoTinta> lista = tintaId != null
        ? consumoRepository.findByTintaIdFetch(tintaId)
        : tipoTrabajo != null
            ? consumoRepository.findByTipoTrabajoOrderByFechaDesc(tipoTrabajo)
            : consumoRepository.findAllByOrderByFechaDesc();
    return lista.stream().map(this::toConsumoDTO).toList();
}
private ConsumoTintaDTO toConsumoDTO(ConsumoTinta c) {
    return ConsumoTintaDTO.builder()
        .id(c.getId())
        .tintaId(c.getTinta().getId())
        .tintaNombre(c.getTinta().getNombre())
        .tintaColor(c.getTinta().getColor())
        .tipoTrabajo(c.getTipoTrabajo())
        .rolloNombre(c.getRolloNombre())
        .rolloCodigo(c.getRolloCodigo())
        .usuario(c.getUsuario())
        .fecha(c.getFecha())
        .nota(c.getNota())
        .build();
}

private TintaDTO toDTO(Tinta tinta) {
    TintaDTO dto = new TintaDTO();
    dto.setId(tinta.getId());
    dto.setNombre(tinta.getNombre());
    dto.setPrecio(tinta.getPrecio());
    dto.setCantidad(tinta.getCantidad());
    dto.setColor(tinta.getColor());
    dto.setMarca(tinta.getMarca());

    if (tinta.getProveedor() != null)
        dto.setProveedorId(tinta.getProveedor().getId());

    if (tinta.getTipoTinta() != null)
        dto.setTipoTintaId(tinta.getTipoTinta().getId());

    return dto;
}
}
