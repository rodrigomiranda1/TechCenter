package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaListadoItemDTO {
    private Long idVenta;
    private String serie;
    private Integer correlativo;
    private String nombreCliente;
    private String dniCliente;
    private String fecha;
    private String hora;
    private String metodoPago;
    private BigDecimal total;
    private String estado;
}
