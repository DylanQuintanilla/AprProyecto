package com.example.repository;

import com.example.model.entity.Consultorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultorioRepository extends JpaRepository<Consultorio, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}