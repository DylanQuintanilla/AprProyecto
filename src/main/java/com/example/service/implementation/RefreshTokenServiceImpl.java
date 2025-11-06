package com.example.service.implementation;

import com.example.config.security.util.JwtUtils;
import com.example.controller.request.auth.RefreshRequest;
import com.example.controller.response.auth.RefreshResponse;
import com.example.model.entity.security.RefreshToken;
import com.example.model.entity.security.UserEntity;
import com.example.repository.security.RefreshTokenRepository;
import com.example.repository.security.UserRepository;
import com.example.service.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${security.jwt.refresh-token.expiration-ms}")
    private Long refreshTokenDurationMs;

    /**
     * Crea un nuevo token de refresco para un usuario.
     * Si ya existe uno, lo elimina y crea uno nuevo.
     */
    @Override
    @Transactional
    public RefreshToken createRefreshToken(String username) {
        UserEntity user = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con username: " + username));

        // Eliminar token de refresco antiguo si existe
        refreshTokenRepository.deleteByUser(user);

        // Crear el nuevo token de refresco
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Valida un token de refresco y emite un nuevo token de acceso.
     */
    @Override
    public RefreshResponse refreshAccessToken(RefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // 1. Encontrar el token en la BD
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new BadCredentialsException("Token de refresco no encontrado en la base de datos"));

        // 2. Verificar si ha expirado
        this.verifyExpiration(refreshToken);

        // 3. Obtener el usuario asociado
        UserEntity user = refreshToken.getUser();

        // 4. Crear un objeto Authentication para pasarlo a JwtUtils
        // (Recreamos los permisos que tiene el usuario)
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        user.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null, // No se necesitan credenciales aquí
                authorities
        );

        // 5. Crear el nuevo token de acceso (JWT)
        String newAccessToken = jwtUtils.createToken(authentication);

        // (Opcional pero recomendado: Rotación de Token de Refresco)
        // Por simplicidad, aquí reutilizamos el mismo token de refresco.

        return new RefreshResponse(newAccessToken, requestRefreshToken);
    }

    /**
     * Método privado para verificar la expiración del token.
     */
    private void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            // Si el token ha expirado, lo eliminamos de la BD y lanzamos un error
            refreshTokenRepository.delete(token);
            throw new BadCredentialsException("El token de refresco ha expirado. Por favor, inicie sesión de nuevo.");
        }
    }
}