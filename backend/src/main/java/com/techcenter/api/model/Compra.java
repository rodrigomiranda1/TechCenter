package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long idcompra;

    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    @Column(name = "fecha_compra")
    private LocalDateTime fechacompra;

    private BigDecimal total;
}
