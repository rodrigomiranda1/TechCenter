package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraRegistroDTO {
    private Long idProveedor;
    private Long idProducto;
    private Integer cantidad;
    private BigDecimal costoUnitario;
}
