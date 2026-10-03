package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVentaDTO {
    private Long id;
    private Integer cantidad;
    private BigDecimal precio;
}
