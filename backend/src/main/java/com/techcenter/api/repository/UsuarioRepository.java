package com.techcenter.api.repository;

import com.techcenter.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByUsernameIgnoreCase(String username);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailIgnoreCase(String email);
    Optional<Usuario> findByGoogleid(String googleId);
    boolean existsByUsername(String username);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
}
