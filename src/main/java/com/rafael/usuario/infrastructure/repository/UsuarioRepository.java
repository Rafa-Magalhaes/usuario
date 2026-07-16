package com.rafael.usuario.infrastructure.repository;

import com.rafael.usuario.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);           // ← Adicionado
    void deleteByEmail(String email);

    // ==================== BUSCAR PERFIL, RETORNA SOMENTE ID====================
    @Query("SELECT u.id FROM Usuario u WHERE u.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);
}