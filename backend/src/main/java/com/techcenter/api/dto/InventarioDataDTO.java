package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDataDTO {
    private List<MovimientoDTO> movimientos;
    private List<ProductoDTO> productos;
    private List<ProductoDTO> stockBajo;
}
