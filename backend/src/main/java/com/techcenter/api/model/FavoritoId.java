package com.techcenter.api.model;

import java.io.Serializable;
import java.util.Objects;

public class FavoritoId implements Serializable {

    private Long cliente;
    private Long producto;

    public FavoritoId() {
    }

    public FavoritoId(Long cliente, Long producto) {
        this.cliente = cliente;
        this.producto = producto;
    }

    public Long getCliente() {
        return cliente;
    }

    public void setCliente(Long cliente) {
        this.cliente = cliente;
    }

    public Long getProducto() {
        return producto;
    }

    public void setProducto(Long producto) {
        this.producto = producto;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FavoritoId other)) {
            return false;
        }
        return Objects.equals(cliente, other.cliente) && Objects.equals(producto, other.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cliente, producto);

    }
}
