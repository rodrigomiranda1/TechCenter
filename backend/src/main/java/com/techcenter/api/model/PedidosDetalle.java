package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "pedido_detalles")
public class PedidosDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_detalle")
    private Long idpedidodetalle;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal preciounitario;

    @Column(name = "total_linea")
    private BigDecimal totallinea;
}
