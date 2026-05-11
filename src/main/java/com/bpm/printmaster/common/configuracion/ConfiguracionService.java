package com.bpm.printmaster.common.configuracion;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    private final ConfiguracionRepository repository;

    public BigDecimal getTipoCambio() {
        return repository.findById("TIPO_CAMBIO_USD")
            .map(c -> new BigDecimal(c.getValor()))
            .orElse(new BigDecimal("6.96")); // valor por defecto
    }

    public void setTipoCambio(BigDecimal valor) {
        Configuracion config = repository.findById("TIPO_CAMBIO_USD")
            .orElse(Configuracion.builder()
                .clave("TIPO_CAMBIO_USD")
                .descripcion("Tipo de cambio USD a Bs.")
                .build());
        config.setValor(valor.toPlainString());
        repository.save(config);
    }

    public Map<String, String> getAll() {
        return Map.of("TIPO_CAMBIO_USD", getTipoCambio().toPlainString());
    }
}