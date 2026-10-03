package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRegistroDTO {
    private Long idProducto;
    private String tipoMovimiento;
    private Integer cantidad;
    private String observacion;
}
