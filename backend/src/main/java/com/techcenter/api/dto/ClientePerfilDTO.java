package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientePerfilDTO {

    private String username;
    private String email;
    private String fotoPerfil;
    private String proveedorAuth;
    private Boolean emailVerificado;

    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
    private Boolean telefonoVerificado;
    private String tipoDocumento;
    private String numeroDocumento;
    private String emailFacturacion;
    private String razonSocial;
    private String direccionFiscal;
}
