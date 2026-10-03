package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {
    private Long idVenta;

    private Long idCliente;
    private Long idUsuario;

    private LocalDateTime fechaVenta;

    private String tipoComprobante;
    private String serie;
    private Integer correlativo;

    private String tipoEntrega;
    private String estado;
    private String observacion;
    private String metodoPago;

    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;

    private List<DetalleVentaDTO> detalles;
}
