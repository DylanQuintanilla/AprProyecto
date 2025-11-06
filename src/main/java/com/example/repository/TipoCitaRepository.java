package com.example.repository;

import com.example.model.entity.TipoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoCitaRepository extends JpaRepository<TipoCita, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}