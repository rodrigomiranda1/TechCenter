package com.techcenter.api.repository;

import com.techcenter.api.model.Categoria;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Categoria c SET c.activo = false WHERE c.idcategoria =:id")
    int desactivarCategoria(@Param("id") Long id);
}
