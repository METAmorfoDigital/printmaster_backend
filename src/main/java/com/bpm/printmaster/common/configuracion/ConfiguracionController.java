package com.bpm.printmaster.common.configuracion;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    @GetMapping("/tipo-cambio")
    public ResponseEntity<Map<String, String>> getTipoCambio() {
        return ResponseEntity.ok(Map.of(
            "valor", configuracionService.getTipoCambio().toPlainString()
        ));
    }

    @PutMapping("/tipo-cambio")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setTipoCambio(@RequestBody Map<String, String> body) {
        configuracionService.setTipoCambio(new BigDecimal(body.get("valor")));
        return ResponseEntity.ok().build();
    }
}
