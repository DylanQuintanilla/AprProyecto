package com.example.repository.security;

import com.example.model.entity.security.RefreshToken;
import com.example.model.entity.security.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Método para buscar un token por su valor
    Optional<RefreshToken> findByToken(String token);

    // Método para eliminar un token perteneciente a un usuario (útil para logout)
    void deleteByUser(UserEntity user);
}