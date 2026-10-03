package com.techcenter.api.repository;

import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.ConsultaIa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaIaRepository extends JpaRepository<ConsultaIa, Long> {
    List<ConsultaIa> findByClienteOrderByFechaconsultaDesc(Cliente cliente);
}
