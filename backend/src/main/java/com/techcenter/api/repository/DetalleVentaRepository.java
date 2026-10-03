package com.techcenter.api.repository;

import com.techcenter.api.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVenta_Idventa(Long idVenta);
}
