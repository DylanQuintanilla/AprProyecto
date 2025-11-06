package com.example.service.implementation;

import com.example.controller.request.EstadoCitaRequest;
import com.example.controller.response.EstadoCitaResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.model.entity.EstadoCita;
import com.example.repository.EstadoCitaRepository;
import com.example.service.EstadoCitaService;
import com.example.service.mapper.EstadoCitaMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoCitaServiceImpl implements EstadoCitaService {

    private final EstadoCitaRepository repository;
    private final EstadoCitaMapper mapper;

    @Override
    public List<EstadoCitaResponse> findAll() {
        return mapper.toEstadoCitaResponseList(repository.findAll());
    }

    @Override
    public EstadoCitaResponse findById(Long id) {
        return mapper.toEstadoCitaResponse(
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Estado de cita no encontrado"))
        );
    }

    @Override
    @Transactional
    public EstadoCitaResponse save(EstadoCitaRequest request) {
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe un estado de cita con ese nombre");
        }
        EstadoCita entity = mapper.toEstadoCita(request);
        return mapper.toEstadoCitaResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public EstadoCitaResponse update(Long id, EstadoCitaRequest request) {
        EstadoCita existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estado de cita no encontrado"));

        if (repository.existsByNombreIgnoreCase(request.getNombre()) &&
                !existing.getNombre().equalsIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe un estado de cita con ese nombre");
        }

        existing.setNombre(request.getNombre());
        return mapper.toEstadoCitaResponse(repository.save(existing));
    }

    @Override
    @Transactional
    public GenericResponse deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Estado de cita no encontrado con ID: " + id);
        }
        repository.deleteById(id);
        return new GenericResponse("Estado de cita con ID " + id + " eliminado exitosamente.");
    }
}