package com.techcenter.api.repository;

import com.techcenter.api.model.Categoria;
import com.techcenter.api.model.Marca;
import com.techcenter.api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    List<Producto> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);

    List<Producto> findByCategoriaAndActivoTrue(Categoria categoria);

    List<Producto> findByMarcaAndActivoTrue(Marca marca);

    List<Producto> findByStockactualLessThanEqual(Integer stockMinimo);

    @Modifying
    @Query("""
    UPDATE Producto p
    SET p.activo = false
    WHERE p.idproducto = :id
    """)
    int desactivarProducto(@Param("id") Long id);
}
