package com.techcenter.api.repository;

import com.techcenter.api.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
}
