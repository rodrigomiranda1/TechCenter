package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private String pasarela;
    private String codigoOperacion;
    private String moneda;
    private BigDecimal monto;
    private String estado;
    private String respuestaPasarela;
}
