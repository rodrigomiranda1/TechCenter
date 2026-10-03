package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaPaginaDTO {
    private List<VentaListadoItemDTO> contenido;
    private int paginaActual;
    private int totalPaginas;
    private long totalElementos;
    private boolean primera;
    private boolean ultima;
}
