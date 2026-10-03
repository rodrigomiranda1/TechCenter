package com.techcenter.api.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idproducto;

    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;

    @Column(name = "stock_actual")
    private Integer stockactual = 0;

    @Column(name = "stock_minimo")
    private Integer stockminimo = 0;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_marca")
    private Marca marca;

    private Boolean activo = true;
}
