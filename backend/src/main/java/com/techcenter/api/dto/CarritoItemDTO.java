package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemDTO {
    private Long idProducto;
    private String codigo;
    private String nombre;
    private BigDecimal precio;
    private Integer cantidad;
    private BigDecimal subtotal;
    private Integer stockActual;
    private String imagenUrl;
}
