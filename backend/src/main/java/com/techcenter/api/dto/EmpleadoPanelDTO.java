package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoPanelDTO {
    private String fechaActual;
    private long ventasHoy;
    private long movimientosHoy;
    private List<ProductoDTO> stockBajo;
    private long totalVentas;
    private long totalPedidos;
    private long totalProductos;
    private long totalMovimientos;
    private List<VentaReporteDTO> ultimasVentas;
}
