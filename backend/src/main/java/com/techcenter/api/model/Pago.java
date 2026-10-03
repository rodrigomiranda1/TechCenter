package com.techcenter.api.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idpago;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    private String pasarela;

    @Column(name = "codigo_operacion")
    private String codigooperacion;

    private String estado;
    private String moneda;
    private BigDecimal monto;

    @Column(name = "fecha_pago")
    private LocalDateTime fechapago;

    @Column(name = "respuesta_pasarela")
    private String respuestapasarela;
}
