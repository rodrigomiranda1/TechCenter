package com.techcenter.api.repository;

import com.techcenter.api.model.Producto;
import com.techcenter.api.model.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {
    List<ProductoImagen> findByProducto(Producto producto);

    Optional<ProductoImagen> findFirstByProductoOrderByIdimagenAsc(Producto producto);
}
