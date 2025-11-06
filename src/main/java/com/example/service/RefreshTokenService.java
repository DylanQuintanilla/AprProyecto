package com.example.service;

import com.example.controller.request.auth.RefreshRequest;
import com.example.controller.response.auth.RefreshResponse;
import com.example.model.entity.security.RefreshToken;

public interface RefreshTokenService {

    /**
     * Crea un nuevo token de refresco para un usuario.
     */
    RefreshToken createRefreshToken(String username);

    /**
     * Valida un token de refresco y, si es válido,
     * genera un nuevo token de acceso (JWT).
     */
    RefreshResponse refreshAccessToken(RefreshRequest request);
}