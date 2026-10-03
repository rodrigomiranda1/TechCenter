package com.techcenter.api.repository;

import com.techcenter.api.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    long countByFechaventaBetween(LocalDateTime inicio, LocalDateTime fin);

    long countByEstado(String estado);

    List<Venta> findTop5ByOrderByFechaventaDesc();

    Optional<Venta> findTopBySerieOrderByCorrelativoDesc(String serie);

    List<Venta> findByFechaventaBetweenOrderByFechaventaDesc(
            LocalDateTime inicio,
            LocalDateTime fin
    );

    @Query("""
           SELECT MAX(v.correlativo)
           FROM Venta v
           WHERE v.serie = :serie
           """)
    Integer obtenerUltimoNumero(@Param("serie") String serie);
}
