package com.techcenter.api.repository;

import com.techcenter.api.model.Pedido;
import com.techcenter.api.model.PedidosDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidosDetalle, Long> {
    List<PedidosDetalle> findByPedido(Pedido pedido);
}
