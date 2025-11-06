package com.example.service.implementation;

import com.example.controller.request.TratamientoRequest;
import com.example.controller.response.TratamientoResponse;
import com.example.model.entity.Enfermedad;
import com.example.model.entity.Tratamiento;
import com.example.repository.EnfermedadRepository;
import com.example.repository.TratamientoRepository;
import com.example.service.TratamientoService;
import com.example.service.mapper.TratamientoMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TratamientoServiceImpl implements TratamientoService {

    private final TratamientoRepository repository;
    private final EnfermedadRepository enfermedadRepository;
    private final TratamientoMapper mapper;

    @Override
    public List<TratamientoResponse> findAll() {
        return mapper.toTratamientoResponseList(repository.findAll());
    }

    @Override
    public TratamientoResponse findById(Long id) {
        return mapper.toTratamientoResponse(
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado"))
        );
    }

    @Override
    public TratamientoResponse save(TratamientoRequest request) {
        Tratamiento tratamiento = mapper.toTratamiento(request);
        Enfermedad enfermedad = enfermedadRepository.findById(request.getEnfermedadId())
                .orElseThrow(() -> new EntityNotFoundException("Enfermedad no encontrada"));
        tratamiento.setEnfermedad(enfermedad);
        return mapper.toTratamientoResponse(repository.save(tratamiento));
    }

    @Override
    public TratamientoResponse update(Long id, TratamientoRequest request) {
        Tratamiento existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado"));

        Enfermedad enfermedad = enfermedadRepository.findById(request.getEnfermedadId())
                .orElseThrow(() -> new EntityNotFoundException("Enfermedad no encontrada"));
        existing.setEnfermedad(enfermedad);
        existing.setNombre(request.getNombre());
        existing.setDescripcion(request.getDescripcion());
        existing.setDuracionEstimada(request.getDuracionEstimada());

        return mapper.toTratamientoResponse(repository.save(existing));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}