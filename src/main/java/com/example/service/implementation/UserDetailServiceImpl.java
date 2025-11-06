package com.example.service.implementation;

import com.example.config.security.util.JwtUtils;
import com.example.controller.request.auth.AuthLoginRequest;
import com.example.controller.request.auth.AuthSignUpRequest;
import com.example.controller.response.auth.AuthResponse;
import com.example.model.entity.Paciente;
import com.example.model.entity.security.RefreshToken;
import com.example.model.entity.security.RoleEntity;
import com.example.model.entity.security.UserEntity;
import com.example.repository.PacienteRepository;
import com.example.repository.security.RoleRepository;
import com.example.repository.security.UserRepository;
import com.example.service.RefreshTokenService;
import jakarta.validation.ValidationException;
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
    private RefreshTokenService refreshTokenService;

    // NUEVA INYECCIÓN PARA LA LÓGICA DE REGISTRO
    @Autowired
    private PacienteRepository pacienteRepository;


    /**
     * Carga el usuario por nombre de usuario (método de Spring Security).
     * Este es el método que se usa internamente para el Log-in.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        // Extrae todos los roles y permisos del usuario
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // 1. Añade los roles con el prefijo "ROLE_"
        userEntity.getRoles().forEach(role ->
                authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        // 2. Añade los permisos granulares (ej. citas:leer:propias)
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

    /**
     * Maneja el registro de un nuevo paciente (Sign-Up) y crea su usuario asociado.
     */
    public AuthResponse createUser(AuthSignUpRequest createRequest) {

        // --- Validaciones de Paciente ---
        if (userRepository.findUserEntityByUsername(createRequest.getUsername()).isPresent()) {
            throw new ValidationException("El nombre de usuario ya está en uso");
        }
        if (pacienteRepository.existsByEmailIgnoreCase(createRequest.getEmail())) {
            throw new ValidationException("Ya existe un paciente con ese correo");
        }
        if (pacienteRepository.existsByDui(createRequest.getDui())) {
            throw new ValidationException("Ya existe un paciente con ese DUI");
        }

        // --- Obtener Rol 'USER' ---
        RoleEntity roleUser = roleRepository.findRoleEntitiesByRoleEnumIn(List.of("USER"))
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error: Rol 'USER' no encontrado. Asegúrese de que el CommandLineRunner haya insertado los roles."));

        // --- Crear UserEntity ---
        UserEntity userEntity = UserEntity.builder()
                .username(createRequest.getUsername())
                .password(passwordEncoder.encode(createRequest.getPassword()))
                .roles(Set.of(roleUser)) // Asignamos solo el rol de usuario
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();

        // --- Crear Paciente ---
        Paciente paciente = Paciente.builder()
                .nombre(createRequest.getNombre())
                .apellido(createRequest.getApellido())
                .email(createRequest.getEmail())
                .dui(createRequest.getDui())
                .fechaNacimiento(createRequest.getFechaNacimiento())
                .numeroTelefono(createRequest.getNumeroTelefono())
                .user(userEntity) // ASOCIAMOS EL USUARIO
                .build();

        // --- Guardar Paciente (y el Usuario en cascada) ---
        Paciente pacienteGuardado = pacienteRepository.save(paciente);
        String username = pacienteGuardado.getUser().getUsername();

        // --- PREPARAR LISTA DE AUTORIDADES (ROLES Y PERMISOS) ---
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // AÑADIMOS LOS ROLES (ej. ROLE_USER)
        userEntity.getRoles().forEach(role ->
                authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        // AÑADIMOS LOS PERMISOS ASOCIADOS (ej. citas:leer:propias)
        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        // --- Preparar Autenticación y Tokens con la lista completa ---
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorityList);

        String accessToken = jwtUtils.createToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

        return new AuthResponse(
                username,
                "Usuario paciente creado exitosamente",
                accessToken,
                refreshToken.getToken(),
                true);
    }

    /**
     * Maneja el inicio de sesión (Log-in) del usuario.
     */
    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.getUsername();
        String password = authLoginRequest.getPassword();

        // 1. Autenticar credenciales
        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Generar Tokens
        String accessToken = jwtUtils.createToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

        AuthResponse authResponse = new AuthResponse(
                username,
                "User loged succesfully",
                accessToken,
                refreshToken.getToken(),
                true);
        return authResponse;
    }

    /**
     * Lógica de autenticación que verifica usuario y contraseña.
     */
    public Authentication authenticate(String username, String password) {
        // Carga el usuario con todos sus roles/permisos
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException(String.format("Invalid username or password"));
        }

        // Verifica si la contraseña coincide (codificada)
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect Password");
        }

        // Retorna el objeto de autenticación con las autoridades completas
        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }
}