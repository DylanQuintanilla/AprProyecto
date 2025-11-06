package com.example.service.implementation;

import com.example.controller.request.TipoCitaRequest;
import com.example.controller.response.TipoCitaResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.model.entity.TipoCita;
import com.example.repository.TipoCitaRepository;
import com.example.service.TipoCitaService;
import com.example.service.mapper.TipoCitaMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoCitaServiceImpl implements TipoCitaService {

    private final TipoCitaRepository repository;
    private final TipoCitaMapper mapper;

    @Override
    public List<TipoCitaResponse> findAll() {
        return mapper.toTipoCitaResponseList(repository.findAll());
    }

    @Override
    public TipoCitaResponse findById(Long id) {
        return mapper.toTipoCitaResponse(
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tipo de cita no encontrado"))
        );
    }

    @Override
    public TipoCitaResponse save(TipoCitaRequest request) {
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe un tipo de cita con ese nombre");
        }
        TipoCita entity = mapper.toTipoCita(request);
        return mapper.toTipoCitaResponse(repository.save(entity));
    }

    @Override
    public TipoCitaResponse update(Long id, TipoCitaRequest request) {
        TipoCita existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de cita no encontrado"));

        if (repository.existsByNombreIgnoreCase(request.getNombre()) &&
                !existing.getNombre().equalsIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe un tipo de cita con ese nombre");
        }

        existing.setNombre(request.getNombre());
        return mapper.toTipoCitaResponse(repository.save(existing));
    }

    @Override
    public GenericResponse deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Tipo de cita no encontrado con ID: " + id);
        }
        repository.deleteById(id);
        return new GenericResponse("Tipo de cita con ID " + id + " eliminado exitosamente.");
    }
}