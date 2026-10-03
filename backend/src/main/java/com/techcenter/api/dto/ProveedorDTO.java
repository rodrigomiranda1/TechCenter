package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {
    private Long idProveedor;
    private String ruc;
    private String razonSocial;
    private String telefono;
    private String correo;
    private String direccion;
}
