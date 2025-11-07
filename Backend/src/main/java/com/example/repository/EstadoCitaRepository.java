package com.example.repository;

import com.example.model.entity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoCitaRepository extends JpaRepository<EstadoCita, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}