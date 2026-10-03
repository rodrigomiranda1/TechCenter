package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaComprobanteDTO {
    private Long idVenta;
    private String tipoComprobante;
    private String serie;
    private Integer correlativo;
    private String fecha;
    private String nombreCliente;
    private String dniCliente;
    private String direccionCliente;
    private String nombreUsuario;
    private String metodoPago;
    private String estado;
    private String tipoEntrega;
    private String observacion;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private List<VentaComprobanteItemDTO> items;
}
