package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoReporteDTO {
    private Long idPedido;
    private String fechaPedido;
    private String nombreCliente;
    private String nombreUsuario;
    private String estado;
    private String direccionEntrega;
    private BigDecimal total;
}
