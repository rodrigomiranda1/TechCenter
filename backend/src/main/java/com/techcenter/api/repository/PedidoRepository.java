package com.techcenter.api.repository;

import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
    List<Pedido> findByClienteOrderByFechapedidoDesc(Cliente cliente);

    List<Pedido> findAllByOrderByFechapedidoDesc();

    long countByFechapedidoBetween(LocalDateTime inicio, LocalDateTime fin);

    long countByEstado(String estado);

    List<Pedido> findByFechapedidoBetweenOrderByFechapedidoDesc(
            LocalDateTime inicio,
            LocalDateTime fin);
}
