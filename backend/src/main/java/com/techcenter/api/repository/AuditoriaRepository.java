package com.techcenter.api.repository;

import com.techcenter.api.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria,Long> {
}
