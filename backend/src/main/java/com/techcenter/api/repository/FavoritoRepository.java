package com.techcenter.api.repository;

import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.Favorito;
import com.techcenter.api.model.FavoritoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {
    List<Favorito> findByCliente(Cliente cliente);
}
