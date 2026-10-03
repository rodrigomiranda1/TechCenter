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
public class PedidoDTO {
    private Long idPedido;
    private Long idCliente;
    private Long idUsuario;
    private LocalDateTime fechaPedido;
    private String estado;

    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;

    private String direccionEntrega;
    private String observacion;

    private String tipoComprobante;
    private String metodoPago;

    private List<PedidoDetalleDTO> detalles;
}
