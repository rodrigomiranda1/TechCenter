package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stockActual;
    private Integer stockMinimo;
    private Long idCategoria;
    private String nombreCategoria;
    private Long idMarca;
    private String nombreMarca;
    private Boolean activo;
    private String imagenUrl;
}
