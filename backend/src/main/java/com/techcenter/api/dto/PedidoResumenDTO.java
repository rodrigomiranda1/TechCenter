package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResumenDTO {
    private Long idPedido;
    private String fechaPedido;
    private String nombreCliente;
    private BigDecimal total;
    private String estado;
}
