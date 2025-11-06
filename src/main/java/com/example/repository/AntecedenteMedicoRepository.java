package com.example.repository;

import com.example.model.entity.AntecedenteMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // <-- AÑADE ESTA IMPORTACIÓN

@Repository
public interface AntecedenteMedicoRepository extends JpaRepository<AntecedenteMedico, Long> {

    // --- AÑADE ESTE MÉTODO ---
    List<AntecedenteMedico> findByPacienteId(Long pacienteId);
}