package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaComprobanteItemDTO {
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal importe;
}
