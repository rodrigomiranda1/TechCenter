package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRapidoDTO {
    private String dni;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
}
