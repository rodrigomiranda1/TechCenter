package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaReporteDTO {
    private Long idVenta;
    private String fechaVenta;
    private String nombreCliente;
    private String nombreUsuario;
    private String tipoComprobante;
    private String serie;
    private Integer correlativo;
    private String metodoPago;
    private String estado;
    private BigDecimal total;
}
