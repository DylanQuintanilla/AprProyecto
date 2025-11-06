package com.example.service.implementation;

import com.example.controller.request.EspecialidadRequest;
import com.example.controller.response.EspecialidadResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.model.entity.Especialidad;
import com.example.repository.EspecialidadRepository;
import com.example.service.EspecialidadService;
import com.example.service.mapper.EspecialidadMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository repository;
    private final EspecialidadMapper mapper;

    @Override
    public List<EspecialidadResponse> findAll() {
        return mapper.toEspecialidadResponseList(repository.findAll());
    }

    @Override
    public EspecialidadResponse findById(Long id) {
        return mapper.toEspecialidadResponse(
                repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con ID: " + id))
        );
    }

    @Override
    @Transactional
    public EspecialidadResponse save(EspecialidadRequest request) {
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe una especialidad con ese nombre");
        }
        Especialidad entity = mapper.toEspecialidad(request);
        return mapper.toEspecialidadResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public EspecialidadResponse update(Long id, EspecialidadRequest request) {
        Especialidad existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con ID: " + id));

        if (repository.existsByNombreIgnoreCase(request.getNombre()) &&
                !existing.getNombre().equalsIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe una especialidad con ese nombre");
        }

        existing.setNombre(request.getNombre());
        return mapper.toEspecialidadResponse(repository.save(existing));
    }

    @Override
    @Transactional
    public GenericResponse deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Especialidad no encontrada con ID: " + id);
        }
        repository.deleteById(id);
        return new GenericResponse("Especialidad con ID " + id + " eliminada exitosamente.");
    }
}