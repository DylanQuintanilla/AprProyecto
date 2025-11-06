package com.example.repository;

import com.example.model.entity.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsuarioIgnoreCase(String usuario);
}