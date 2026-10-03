package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "valoraciones")
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion")
    private Long idvaloracion;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    private Integer estrellas;
    private String comentario;

}
