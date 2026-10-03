package com.techcenter.api.repository;

import com.techcenter.api.model.Comprobante;
import com.techcenter.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    Optional<Comprobante> findByPedido(Pedido pedido);
    Optional<Comprobante> findTopByTipoAndSerieOrderByCorrelativoDesc(String tipo, String serie);
}
