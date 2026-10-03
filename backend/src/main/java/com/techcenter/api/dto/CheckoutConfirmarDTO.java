package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutConfirmarDTO {
    private String direccionEntrega;
    private String tipoComprobante;
    private String metodoPago;
    private String numeroTarjeta;
    private String codigoOperacion;
}
