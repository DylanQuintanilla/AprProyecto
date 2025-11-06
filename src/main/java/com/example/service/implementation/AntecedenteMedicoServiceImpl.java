package com.example.service.implementation;

import com.example.controller.request.AntecedenteMedicoRequest;
import com.example.controller.response.AntecedenteMedicoResponse;
import com.example.model.entity.AntecedenteMedico;
import com.example.model.entity.Paciente;
import com.example.repository.AntecedenteMedicoRepository;
import com.example.repository.PacienteRepository;
import com.example.service.AntecedenteMedicoService;
import com.example.service.mapper.AntecedenteMedicoMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AntecedenteMedicoServiceImpl implements AntecedenteMedicoService {

    private final AntecedenteMedicoRepository repository;
    private final PacienteRepository pacienteRepository;
    private final AntecedenteMedicoMapper mapper;

    @Override
    public List<AntecedenteMedicoResponse> findAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (hasAuthority(authentication, "antecedentes:admin")) {
            // Admin ve todos los antecedentes
            return mapper.toAntecedenteMedicoResponseList(repository.findAll());
        }

        if (hasAuthority(authentication, "antecedentes:leer:propios")) {
            // User (Paciente) ve solo los suyos
            Paciente paciente = pacienteRepository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
            return mapper.toAntecedenteMedicoResponseList(repository.findByPacienteId(paciente.getId()));
        }

        // Un Doctor no debería usar este endpoint (es muy general).
        // Debería obtener los antecedentes a través del perfil de un paciente.
        if (hasAuthority(authentication, "antecedentes:leer:paciente")) {
            // Devolvemos vacío para el doctor, para forzar que use un endpoint más específico.
            return Collections.emptyList();
        }

        throw new AccessDeniedException("No tiene permisos para ver esta lista de antecedentes");
    }

    @Override
    public AntecedenteMedicoResponse findById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        AntecedenteMedico antecedente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Antecedente médico no encontrado"));

        if (hasAuthority(authentication, "antecedentes:admin") || hasAuthority(authentication, "antecedentes:leer:paciente")) {
            // Admin o Doctor pueden ver cualquier antecedente por ID
            return mapper.toAntecedenteMedicoResponse(antecedente);
        }

        if (hasAuthority(authentication, "antecedentes:leer:propios")) {
            // User (Paciente) solo puede ver uno propio
            Paciente pacientePropio = pacienteRepository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

            if (!antecedente.getPaciente().getId().equals(pacientePropio.getId())) {
                throw new AccessDeniedException("No tiene permisos para ver este antecedente");
            }
            return mapper.toAntecedenteMedicoResponse(antecedente);
        }

        throw new AccessDeniedException("No tiene permisos para ver este antecedente");
    }

    @Override
    public AntecedenteMedicoResponse save(AntecedenteMedicoRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Solo Admin o Doctor (con permiso de gestionar) pueden crear
        if (!hasAuthority(authentication, "antecedentes:admin") && !hasAuthority(authentication, "antecedentes:gestionar:paciente")) {
            throw new AccessDeniedException("No tiene permisos para crear antecedentes médicos");
        }

        // (Un Doctor solo debería poder crear para sus pacientes, pero esa lógica es más compleja)

        AntecedenteMedico antecedente = mapper.toAntecedenteMedico(request);
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        antecedente.setPaciente(paciente);
        return mapper.toAntecedenteMedicoResponse(repository.save(antecedente));
    }

    @Override
    public AntecedenteMedicoResponse update(Long id, AntecedenteMedicoRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Solo Admin o Doctor pueden actualizar
        if (!hasAuthority(authentication, "antecedentes:admin") && !hasAuthority(authentication, "antecedentes:gestionar:paciente")) {
            throw new AccessDeniedException("No tiene permisos para actualizar antecedentes médicos");
        }

        AntecedenteMedico existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Antecedente médico no encontrado"));

        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        existing.setPaciente(paciente);
        existing.setDescripcion(request.getDescripcion());

        return mapper.toAntecedenteMedicoResponse(repository.save(existing));
    }

    @Override
    public void deleteById(Long id) {
        // La seguridad de este método ya está manejada por SecurityConfig
        // (Solo "antecedentes:admin" puede entrar aquí)
        repository.deleteById(id);
    }

    // --- Método de ayuda ---
    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(authority));
    }
}