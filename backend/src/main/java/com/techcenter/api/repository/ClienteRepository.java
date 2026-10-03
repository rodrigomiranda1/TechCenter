package com.techcenter.api.repository;

import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuario(Usuario usuario);
    Optional<Cliente> findByDni(String dni);
    List<Cliente> findAllByDni(String dni);
}
