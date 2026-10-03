package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteDTO {
    private Long idComprobante;
    private String tipo;
    private String serie;
    private Integer correlativo;
    private String fechaEmision;
    private String estado;
}
