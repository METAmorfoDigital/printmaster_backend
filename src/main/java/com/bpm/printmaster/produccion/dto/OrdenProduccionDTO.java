package com.bpm.printmaster.produccion.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdenProduccionDTO {

    private Long id;

    // Solo en respuesta, no en request
    private Integer correlativo;
    private Integer anio;
    private String codigoRecibo; // "22/26"

    @NotBlank(message = "Cliente requerido")
    private String cliente;

    @NotNull(message = "Fecha requerida")
    private LocalDate fecha;

    private LocalDate fechaEntrega;
    
    private Long rolloId;

    private String rolloNombre; // solo en respuesta

    // ── Impresión ──
  
    private BigDecimal metraje;


    private BigDecimal costoImpresion;
    private BigDecimal subtotalImpresion; 

    // ── Planchado ──
    private BigDecimal cantidadPlanchado;
    private BigDecimal costoPlanchado;
    private BigDecimal subtotalPlanchado;

    // ── Insignias T ──
    private BigDecimal cantidadInsignias;
    private BigDecimal costoInsignias;
    private BigDecimal subtotalInsignias;

    // ── Diseño ──
    private BigDecimal costoDiseno;

    // ── Total (calculado en backend) ──
    private BigDecimal total;

    // ── Pago ──
    private String tipoPago;
    private String banco;

    private String estadoPago; // "PENDIENTE", "PARCIAL", "PAGADO"

    private LocalDate fechaPago;
    private Boolean pagado;
    
    
    //para pagos
    private Long       cobradorId;      // para recibir desde el frontend al guardar
    private Long       qrId;            // para recibir desde el frontend al guardar
 
    private String     cobradorNombre;  // para mostrar en planilla
    private String     qrBanco;         // para mostrar en planilla
    private String     qrImagenBase64;  // para descargar QR desde planilla

    
    // ── Tipo de trabajo ──
    @NotBlank(message = "Tipo de trabajo requerido")
    private String tipoTrabajo; // DTF | DTF_PLUS | SUBLIMADO
}