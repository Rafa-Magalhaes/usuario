package com.rafael.usuario.infrastructure.repository;

import com.rafael.usuario.infrastructure.entity.Telefones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelefonesRepository extends JpaRepository <Telefones, Long> {
}
