package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {
    private Long idMovimiento;
    private String fechaMovimiento;
    private String nombreProducto;
    private String tipoMovimiento;
    private Integer cantidad;
    private String observacion;
}
