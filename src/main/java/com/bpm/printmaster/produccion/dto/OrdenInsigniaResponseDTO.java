// OrdenInsigniaResponseDTO.java — para devolver al frontend
package com.bpm.printmaster.produccion.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data 
@Builder
@NoArgsConstructor @AllArgsConstructor
public class OrdenInsigniaResponseDTO {
    private Long id;
    private Integer correlativo;
    private Integer anio;
    private String codigoRecibo;
    private String cliente;
    private LocalDate fecha;
    private LocalDate fechaEntrega;
    private String observaciones;
    private BigDecimal total;
    private String tipoPago;
    private String banco;
    private LocalDate fechaPago;
    private String estadoPago; // ← agregar
    private boolean pagado;
    private List<DetalleInsigniaDTO> detalles;

    //para pagos
   private Long cobradorId;
    private String cobradorNombre;
    private Long qrId;
    private String qrBanco;
    private String qrImagenBase64;
}