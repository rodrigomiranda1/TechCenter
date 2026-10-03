package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private Long iddetalleventa;

    @ManyToOne
    @JoinColumn(name = "id_venta")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal preciounitario;

    public Long getIdDetalleVenta() {
        return iddetalleventa;
    }

    public BigDecimal getImporte() {
        return preciounitario.multiply(BigDecimal.valueOf(cantidad));
    }

}
