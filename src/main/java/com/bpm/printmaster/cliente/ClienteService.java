package com.bpm.printmaster.cliente;




import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // ── Buscar por nombre (autocomplete) ────────────────────────
    public List<ClienteDTO> buscar(String query) {
        return clienteRepository
                .findByNombreContainingIgnoreCase(query.trim())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Listar todos ─────────────────────────────────────────────
    public List<ClienteDTO> getAll() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Crear nuevo cliente ──────────────────────────────────────
    public ClienteDTO save(ClienteDTO dto) {
        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre().trim())
                .celular(dto.getCelular())
                .correo(dto.getCorreo())
                .direccion(dto.getDireccion())
                .build();

        return toDTO(clienteRepository.save(cliente));
    }

    // ── Mapper ───────────────────────────────────────────────────
    private ClienteDTO toDTO(Cliente c) {
        return ClienteDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .celular(c.getCelular())
                .correo(c.getCorreo())
                .direccion(c.getDireccion())
                .build();
    }
}