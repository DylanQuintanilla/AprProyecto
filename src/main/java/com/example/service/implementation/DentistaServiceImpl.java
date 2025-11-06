package com.example.service.implementation;

import com.example.controller.request.DentistaRequest;
import com.example.controller.response.DentistaResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.model.entity.Dentista;
import com.example.model.entity.Especialidad;
import com.example.model.entity.security.RoleEntity;
import com.example.model.entity.security.UserEntity;
import com.example.repository.DentistaRepository;
import com.example.repository.EspecialidadRepository;
import com.example.repository.security.RoleRepository;
import com.example.repository.security.UserRepository;
import com.example.service.DentistaService;
import com.example.service.mapper.DentistaMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DentistaServiceImpl implements DentistaService {

    @Autowired
    private final DentistaRepository repository;

    @Autowired
    private final EspecialidadRepository especialidadRepository;

    @Autowired
    private final DentistaMapper mapper;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<DentistaResponse> findAll() {
        return mapper.toDentistaResponseList(repository.findAll());
    }

    @Override
    public DentistaResponse findById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (hasAuthority(authentication, "dentistas:admin")) {
            Dentista dentista = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado"));
            return mapper.toDentistaResponse(dentista);
        }

        if (hasAuthority(authentication, "perfil:leer:propio")) {
            Dentista dentistaPropio = repository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado para el usuario"));

            if (!dentistaPropio.getId().equals(id)) {
                throw new AccessDeniedException("No tiene permisos para ver el perfil de otro dentista");
            }
            return mapper.toDentistaResponse(dentistaPropio);
        }

        throw new AccessDeniedException("No tiene permisos para ver este perfil");
    }

    @Override
    @Transactional
    public DentistaResponse save(DentistaRequest request) {

        // --- Validaciones (Asegurarse de que no exista el correo ni el usuario) ---
        if (repository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ValidationException("Ya existe un dentista con ese correo");
        }
        // Usamos userRepository para verificar que el username no exista
        if (userRepository.findUserEntityByUsername(request.getUsuario()).isPresent()) {
            throw new ValidationException("El nombre de usuario ya está en uso");
        }

        // --- LÓGICA DE USUARIO AÑADIDA ---
        // Buscamos el rol DOCTOR
        RoleEntity roleDoctor = roleRepository.findRoleEntitiesByRoleEnumIn(List.of("DOCTOR"))
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error: Rol 'DOCTOR' no encontrado."));

        // Creamos la entidad de Usuario
        UserEntity userEntity = UserEntity.builder()
                .username(request.getUsuario())
                .password(passwordEncoder.encode(request.getContrasena()))
                .roles(Set.of(roleDoctor)) // <-- Asignamos el rol DOCTOR
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();

        Dentista dentista = mapper.toDentista(request);
        dentista.setUser(userEntity); // <-- ASOCIAMOS EL USUARIO

        // (Tu lógica de especialidades que ya tenías)
        if (request.getEspecialidadesIds() != null && !request.getEspecialidadesIds().isEmpty()) {
            List<Especialidad> especialidades = especialidadRepository.findAllById(request.getEspecialidadesIds());
            if (especialidades.size() != request.getEspecialidadesIds().size()) {
                throw new ValidationException("Una o más especialidades no existen");
            }
            dentista.setEspecialidades(especialidades);
        }

        // Guardamos el dentista (y el usuario en cascada [cite: 456])
        Dentista dentistaGuardado = repository.save(dentista);

        // --- EXTRAER PERMISOS Y ROLES COMPLETOS ---
        // Aunque no es necesario para el save, es una buena práctica retornarlo completo
        // para futuros usos. El DentistaResponse se genera del objeto guardado.

        // Esto es opcional, ya que el mapper se encarga de la respuesta.
        // Lo importante era la creación del UserEntity.

        return mapper.toDentistaResponse(dentistaGuardado);
    }

    @Override
    @Transactional
    public DentistaResponse update(Long id, DentistaRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Dentista existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado"));

        boolean canUpdate = false;
        if (hasAuthority(authentication, "dentistas:admin")) {
            canUpdate = true;
        } else if (hasAuthority(authentication, "perfil:actualizar:propio")) {
            Dentista dentistaPropio = repository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado para el usuario"));
            if (dentistaPropio.getId().equals(id)) {
                canUpdate = true;
            }
        }

        if (!canUpdate) {
            throw new AccessDeniedException("No tiene permisos para actualizar este perfil");
        }

        if (!existing.getEmail().equalsIgnoreCase(request.getEmail()) &&
                repository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ValidationException("Ya existe un dentista con ese correo");
        }
        // --- BLOQUE DE USUARIO ELIMINADO ---

        existing.setNombre(request.getNombre());
        existing.setApellido(request.getApellido());
        existing.setEmail(request.getEmail());

        if (request.getEspecialidadesIds() != null) {
            List<Especialidad> especialidades = especialidadRepository.findAllById(request.getEspecialidadesIds());
            if (especialidades.size() != request.getEspecialidadesIds().size()) {
                throw new ValidationException("Una o más especialidades no existen");
            }
            existing.setEspecialidades(especialidades);
        }

        return mapper.toDentistaResponse(repository.save(existing));
    }

    @Override
    @Transactional
    public GenericResponse deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Dentista no encontrado con ID: " + id);
        }
        repository.deleteById(id);
        return new GenericResponse("Dentista con ID " + id + " eliminado exitosamente.");
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(authority));
    }
}