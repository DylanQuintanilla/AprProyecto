package com.example.service.implementation;

import com.example.controller.request.ConsultorioRequest;
import com.example.controller.response.ConsultorioResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.model.entity.Consultorio;
import com.example.repository.ConsultorioRepository;
import com.example.service.ConsultorioService;
import com.example.service.mapper.ConsultorioMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultorioServiceImpl implements ConsultorioService {

    private final ConsultorioRepository repository;
    private final ConsultorioMapper mapper;

    @Override
    public List<ConsultorioResponse> findAll() {
        return mapper.toConsultorioResponseList(repository.findAll());
    }

    @Override
    public ConsultorioResponse findById(Long id) {
        return mapper.toConsultorioResponse(
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Consultorio no encontrado"))
        );
    }

    @Override
    public ConsultorioResponse save(ConsultorioRequest request) {
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe un consultorio con ese nombre");
        }
        Consultorio entity = mapper.toConsultorio(request);
        return mapper.toConsultorioResponse(repository.save(entity));
    }

    @Override
    public ConsultorioResponse update(Long id, ConsultorioRequest request) {
        Consultorio existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultorio no encontrado"));

        if (repository.existsByNombreIgnoreCase(request.getNombre()) &&
                !existing.getNombre().equalsIgnoreCase(request.getNombre())) {
            throw new ValidationException("Ya existe un consultorio con ese nombre");
        }

        existing.setNombre(request.getNombre());
        existing.setUbicacion(request.getUbicacion());
        return mapper.toConsultorioResponse(repository.save(existing));
    }

    @Override
    public GenericResponse deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Consultorio no encontrado con ID: " + id);
        }
        repository.deleteById(id);
        return new GenericResponse("Consultorio con ID " + id + " eliminado exitosamente.");
    }
}