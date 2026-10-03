package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idventa;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaventa;

    @Column(name = "tipo_comprobante")
    private String tipocomprobante;

    private String serie;

    private Integer correlativo;

    @Column(name = "tipo_entrega")
    private String tipoentrega;

    private String estado;

    private String observacion;

    @Column(name = "metodo_pago")
    private String metodopago;

    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;

}
