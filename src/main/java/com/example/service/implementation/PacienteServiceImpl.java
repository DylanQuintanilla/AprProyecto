package com.example.service.implementation;

import com.example.controller.request.PacienteRequest;
import com.example.controller.response.PacienteResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.model.entity.Paciente;
import com.example.repository.PacienteRepository;
import com.example.service.PacienteService;
import com.example.service.mapper.PacienteMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository repository;
    private final PacienteMapper mapper;

    @Override
    public List<PacienteResponse> findAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (hasAuthority(authentication, "pacientes:admin") || hasAuthority(authentication, "pacientes:leer:lista")) {
            return mapper.toPacienteResponseList(repository.findAll());
        }

        throw new AccessDeniedException("No tiene permisos para listar todos los pacientes");
    }

    @Override
    public PacienteResponse findById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (hasAuthority(authentication, "pacientes:admin") || hasAuthority(authentication, "pacientes:leer:perfil")) {
            Paciente paciente = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
            return mapper.toPacienteResponse(paciente);
        }

        if (hasAuthority(authentication, "perfil:leer:propio")) {
            Paciente pacientePropio = repository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado para el usuario"));

            if (!pacientePropio.getId().equals(id)) {
                throw new AccessDeniedException("No tiene permisos para ver el perfil de otro paciente");
            }
            return mapper.toPacienteResponse(pacientePropio);
        }

        throw new AccessDeniedException("No tiene permisos para ver este perfil");
    }

    @Override
    public PacienteResponse save(PacienteRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!hasAuthority(authentication, "pacientes:admin")) {
            throw new AccessDeniedException("No tiene permisos para crear un nuevo paciente");
        }

        if (repository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ValidationException("Ya existe un paciente con ese correo");
        }
        // --- BLOQUE DE USUARIO ELIMINADO ---
        if (repository.existsByDui(request.getDui())) {
            throw new ValidationException("Ya existe un paciente con ese DUI");
        }
        Paciente entity = mapper.toPaciente(request);

        return mapper.toPacienteResponse(repository.save(entity));
    }

    @Override
    public PacienteResponse update(Long id, PacienteRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Paciente existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        boolean canUpdate = false;
        if (hasAuthority(authentication, "pacientes:admin")) {
            canUpdate = true;
        } else if (hasAuthority(authentication, "perfil:actualizar:propio")) {
            Paciente pacientePropio = repository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado para el usuario"));
            if (pacientePropio.getId().equals(id)) {
                canUpdate = true;
            }
        }

        if (!canUpdate) {
            throw new AccessDeniedException("No tiene permisos para actualizar este perfil");
        }

        if (!existing.getEmail().equalsIgnoreCase(request.getEmail()) &&
                repository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ValidationException("Ya existe un paciente con ese correo");
        }
        // --- BLOQUE DE USUARIO ELIMINADO ---
        if (!existing.getDui().equals(request.getDui()) &&
                repository.existsByDui(request.getDui())) {
            throw new ValidationException("Ya existe un paciente con ese DUI");
        }

        existing.setNombre(request.getNombre());
        existing.setApellido(request.getApellido());
        existing.setFechaNacimiento(request.getFechaNacimiento());
        existing.setNumeroTelefono(request.getNumeroTelefono());
        existing.setEmail(request.getEmail());
        existing.setDui(request.getDui());

        return mapper.toPacienteResponse(repository.save(existing));
    }

    @Override
    public GenericResponse deleteById(Long id) {
        repository.deleteById(id);
        return new GenericResponse("Paciente con ID " + id + " eliminado exitosamente.");
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(authority));
    }
}