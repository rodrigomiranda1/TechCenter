package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteAdminDTO {
    private Long idCliente;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String direccion;
    private String tipoDocumento;
    private String numeroDocumento;
    private String emailFacturacion;
    private String razonSocial;
    private String direccionFiscal;
}
