package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {
    private Long idCompra;
    private String fechaCompra;
    private String nombreProveedor;
    private BigDecimal total;
}
