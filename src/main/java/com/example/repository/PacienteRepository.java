package com.example.repository;

import com.example.model.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsuarioIgnoreCase(String usuario);
    boolean existsByDui(String dui);
}