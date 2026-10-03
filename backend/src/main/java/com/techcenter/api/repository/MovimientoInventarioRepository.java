package com.techcenter.api.repository;

import com.techcenter.api.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    long countByFechamovimientoBetween(LocalDateTime inicio, LocalDateTime fin);
}
