package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idpedido;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechapedido;

    private String estado;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;

    @Column(name = "direccion_entrega")
    private String direccionentrega;

    private String observacion;
}
