package com.example.repository;

import com.example.model.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // <-- AÑADE ESTA IMPORTACIÓN

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // --- AÑADE ESTOS MÉTODOS ---
    List<Cita> findByPacienteId(Long pacienteId);
    List<Cita> findByDentistaId(Long dentistaId);
}