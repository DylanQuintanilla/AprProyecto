package com.example.repository;

import com.example.model.entity.Enfermedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnfermedadRepository extends JpaRepository<Enfermedad, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}