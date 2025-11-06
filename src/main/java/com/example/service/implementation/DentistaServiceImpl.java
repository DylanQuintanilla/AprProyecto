package com.example.service.implementation;

import com.example.controller.request.DentistaRequest;
import com.example.controller.response.DentistaResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.model.entity.Dentista;
import com.example.model.entity.Especialidad;
import com.example.repository.DentistaRepository;
import com.example.repository.EspecialidadRepository;
import com.example.service.DentistaService;
import com.example.service.mapper.DentistaMapper;
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
public class DentistaServiceImpl implements DentistaService {

    private final DentistaRepository repository;
    private final EspecialidadRepository especialidadRepository;
    private final DentistaMapper mapper;

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
    public DentistaResponse save(DentistaRequest request) {

        if (repository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ValidationException("Ya existe un dentista con ese correo");
        }
        // --- BLOQUE DE USUARIO ELIMINADO ---

        Dentista dentista = mapper.toDentista(request);

        if (request.getEspecialidadesIds() != null && !request.getEspecialidadesIds().isEmpty()) {
            List<Especialidad> especialidades = especialidadRepository.findAllById(request.getEspecialidadesIds());
            if (especialidades.size() != request.getEspecialidadesIds().size()) {
                throw new ValidationException("Una o más especialidades no existen");
            }
            dentista.setEspecialidades(especialidades);
        }

        return mapper.toDentistaResponse(repository.save(dentista));
    }

    @Override
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