package com.example.service.implementation;

import com.example.controller.request.EstadoCitaRequest;
import com.example.controller.response.EstadoCitaResponse;
import com.example.model.entity.EstadoCita;
import com.example.repository.EstadoCitaRepository;
import com.example.service.EstadoCitaService;
import com.example.service.mapper.EstadoCitaMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public EstadoCitaResponse save(EstadoCitaRequest request) {
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe un estado de cita con ese nombre");
        }
        EstadoCita entity = mapper.toEstadoCita(request);
        return mapper.toEstadoCitaResponse(repository.save(entity));
    }

    @Override
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
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}