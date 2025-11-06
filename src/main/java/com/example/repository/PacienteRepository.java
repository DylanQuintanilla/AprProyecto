package com.example.repository;

import com.example.model.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // <-- AÑADE ESTA IMPORTACIÓN

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByDui(String dui);

    // --- AÑADE ESTE MÉTODO ---
    Optional<Paciente> findByUserUsername(String username);
}