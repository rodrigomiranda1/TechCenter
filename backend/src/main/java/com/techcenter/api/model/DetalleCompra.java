package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "detalle_compra")
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_compra")
    private Long iddetallecompra;

    @ManyToOne
    @JoinColumn(name = "id_compra")
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    private Integer cantidad;

    @Column(name = "costo_unitario")
    private BigDecimal costounitario;
}
