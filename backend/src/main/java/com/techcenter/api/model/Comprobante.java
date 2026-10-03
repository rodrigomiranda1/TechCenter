package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comprobantes")
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Long idcomprobante;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    private String tipo;
    private String serie;
    private Integer correlativo;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaemision;

    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String estado;

    @Column(name = "pdf_url")
    private String pdfurl;

    private String observacion;
}
