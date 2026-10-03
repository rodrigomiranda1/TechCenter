package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoAdminDTO {
    private Long idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stockActual;
    private Integer stockMinimo;
    private Long idCategoria;
    private Long idMarca;
    private String urlImagen;
    private Boolean activo;
}
