package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaIaDTO {
    private Long idConsulta;
    private String pregunta;
    private String respuesta;
    private String fechaConsulta;
}
