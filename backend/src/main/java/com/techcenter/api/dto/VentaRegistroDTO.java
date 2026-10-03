package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRegistroDTO {
    private Long idCliente;
    private Long idUsuario;
    private String tipoComprobante;
    private String serie;
    private Integer correlativo;
    private String tipoEntrega;
    private String observacion;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String metodoPago;
    private List<ItemVentaDTO> detalle;
}
