package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "producto_imagenes")
public class ProductoImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long idimagen;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Column(name = "url_imagen")
    private String urlimagen;

}
