package com.example.service.implementation;

import com.example.controller.request.EnfermedadRequest;
import com.example.controller.response.EnfermedadResponse;
import com.example.model.entity.Enfermedad;
import com.example.repository.EnfermedadRepository;
import com.example.service.EnfermedadService;
import com.example.service.mapper.EnfermedadMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnfermedadServiceImpl implements EnfermedadService {

    private final EnfermedadRepository repository;
    private final EnfermedadMapper mapper;

    @Override
    public List<EnfermedadResponse> findAll() {
        return mapper.toEnfermedadResponseList(repository.findAll());
    }

    @Override
    public EnfermedadResponse findById(Long id) {
        return mapper.toEnfermedadResponse(
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Enfermedad no encontrada"))
        );
    }

    @Override
    public EnfermedadResponse save(EnfermedadRequest request) {
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe una enfermedad con ese nombre");
        }
        Enfermedad entity = mapper.toEnfermedad(request);
        return mapper.toEnfermedadResponse(repository.save(entity));
    }

    @Override
    public EnfermedadResponse update(Long id, EnfermedadRequest request) {
        Enfermedad existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enfermedad no encontrada"));

        if (repository.existsByNombreIgnoreCase(request.getNombre()) &&
                !existing.getNombre().equalsIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe una enfermedad con ese nombre");
        }

        existing.setNombre(request.getNombre());
        existing.setDescripcion(request.getDescripcion());
        return mapper.toEnfermedadResponse(repository.save(existing));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}