package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoConfirmacionDTO {
    private Long idPedido;
    private String fechaPedido;
    private String estado;
    private String nombreCliente;
    private String documentoCliente;
    private String direccionEntrega;
    private String observacion;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private List<PedidoDetalleDTO> detalles;
    private PagoDTO pago;
    private ComprobanteDTO comprobante;
}
