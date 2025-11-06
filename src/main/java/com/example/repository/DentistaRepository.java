package com.example.repository;

import com.example.model.entity.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // <-- AÑADE ESTA IMPORTACIÓN

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {
    boolean existsByEmailIgnoreCase(String email);

    // --- AÑADE ESTE MÉTODO ---
    Optional<Dentista> findByUserUsername(String username);
}