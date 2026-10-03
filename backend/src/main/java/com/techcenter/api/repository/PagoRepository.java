package com.techcenter.api.repository;

import com.techcenter.api.model.Pago;
import com.techcenter.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findFirstByPedidoOrderByIdpagoDesc(Pedido pedido);
}
