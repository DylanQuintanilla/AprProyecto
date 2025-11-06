package com.example.service.implementation;

import com.example.controller.request.auth.AuthCreateUserRequest;
import com.example.controller.request.auth.AuthLoginRequest;
import com.example.controller.response.auth.AuthResponse;
import com.example.model.entity.security.RefreshToken;
import com.example.model.entity.security.RoleEntity;
import com.example.model.entity.security.UserEntity;
import com.example.repository.security.RoleRepository;
import com.example.repository.security.UserRepository;
import com.example.config.security.util.JwtUtils;
import com.example.service.RefreshTokenService; // <-- 1. Importar el nuevo servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenService refreshTokenService; // <-- 2. Inyectar el nuevo servicio

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userEntity.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }

    public AuthResponse createUser(AuthCreateUserRequest createRoleRequest) {
        String username = createRoleRequest.getUsername();
        String password = createRoleRequest.getPassword();
        List<String> rolesRequest = createRoleRequest.getRoleRequest().getRoleListName();

        Set<RoleEntity> roleEntityList = roleRepository.findRoleEntitiesByRoleEnumIn(rolesRequest).stream().collect(Collectors.toSet());
        if (roleEntityList.isEmpty()) {
            throw new IllegalArgumentException("The roles specified does not exist.");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roleEntityList)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();
        UserEntity userSaved = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        userSaved.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        userSaved.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userSaved, null, authorities);

        // --- 3. Lógica actualizada para CREAR USUARIO ---
        String accessToken = jwtUtils.createToken(authentication); // Token de Acceso
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username); // Token de Refresco

        AuthResponse authResponse = new AuthResponse(
                username,
                "User created successfully",
                accessToken, // <-- 4. Corregido de 'jwt' a 'accessToken'
                refreshToken.getToken(), // <-- 5. Añadir el token de refresco
                true);
        return authResponse;
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.getUsername();
        String password = authLoginRequest.getPassword();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // --- 6. Lógica actualizada para LOGIN ---
        String accessToken = jwtUtils.createToken(authentication); // Token de Acceso
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username); // Token de Refresco

        AuthResponse authResponse = new AuthResponse(
                username,
                "User loged succesfully",
                accessToken, // <-- Corregido de 'jwt' a 'accessToken'
                refreshToken.getToken(), // <-- Añadir el token de refresco
                true);
        return authResponse;
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);
        if (userDetails == null) {
            throw new BadCredentialsException(String.format("Invalid username or password"));
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect Password");
        }

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }
}