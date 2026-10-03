package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "favoritos")
@IdClass(FavoritoId.class)
public class Favorito {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;
}
