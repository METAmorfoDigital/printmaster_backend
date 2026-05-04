package com.bpm.printmaster.cliente;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // GET /api/clientes?q=juan  → autocomplete
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> buscar(
            @RequestParam(name = "q", defaultValue = "") String q) {

        List<ClienteDTO> resultado = q.isBlank()
                ? clienteService.getAll()
                : clienteService.buscar(q);

        return ResponseEntity.ok(resultado);
    }

    // POST /api/clientes  → crear nuevo cliente
    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.save(dto));
    }
}