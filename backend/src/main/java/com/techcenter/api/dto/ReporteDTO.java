package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDTO {
    private List<VentaReporteDTO> ventas;
    private long cantidadVentas;
    private BigDecimal totalVentas;

    private List<PedidoReporteDTO> pedidos;
    private long cantidadPedidos;
    private BigDecimal totalPedidos;
}
