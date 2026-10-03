package com.techcenter.api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaDTO {
    private String fecha;
    private String accion;
    private String entidad;
}
