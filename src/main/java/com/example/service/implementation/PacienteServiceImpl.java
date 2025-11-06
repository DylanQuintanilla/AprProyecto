package com.example.service.implementation;

import com.example.controller.request.PacienteRequest;
import com.example.controller.response.PacienteResponse;
import com.example.model.entity.Paciente;
import com.example.repository.PacienteRepository;
import com.example.service.PacienteService;
import com.example.service.mapper.PacienteMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository repository;
    private final PacienteMapper mapper;

    @Override
    public List<PacienteResponse> findAll() {
        return mapper.toPacienteResponseList(repository.findAll());
    }

    @Override
    public PacienteResponse findById(Long id) {
        return mapper.toPacienteResponse(
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"))
        );
    }

    @Override
    public PacienteResponse save(PacienteRequest request) {
        if (repository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ValidationException("Ya existe un paciente con ese correo");
        }
        if (repository.existsByUsuarioIgnoreCase(request.getUsuario())) {
            throw new ValidationException("Ya existe un paciente con ese nombre de usuario");
        }
        if (repository.existsByDui(request.getDui())) {
            throw new ValidationException("Ya existe un paciente con ese DUI");
        }
        Paciente entity = mapper.toPaciente(request);
        return mapper.toPacienteResponse(repository.save(entity));
    }

    @Override
    public PacienteResponse update(Long id, PacienteRequest request) {
        Paciente existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        if (!existing.getEmail().equalsIgnoreCase(request.getEmail()) &&
                repository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ValidationException("Ya existe un paciente con ese correo");
        }
        if (request.getUsuario() != null && !existing.getUsuario().equalsIgnoreCase(request.getUsuario()) &&
                repository.existsByUsuarioIgnoreCase(request.getUsuario())) {
            throw new ValidationException("Ya existe un paciente con ese nombre de usuario");
        }
        if (!existing.getDui().equals(request.getDui()) &&
                repository.existsByDui(request.getDui())) {
            throw new ValidationException("Ya existe un paciente con ese DUI");
        }

        existing.setNombre(request.getNombre());
        existing.setApellido(request.getApellido());
        existing.setFechaNacimiento(request.getFechaNacimiento());
        existing.setNumeroTelefono(request.getNumeroTelefono());
        existing.setEmail(request.getEmail());
        existing.setUsuario(request.getUsuario());
        existing.setDui(request.getDui());

        return mapper.toPacienteResponse(repository.save(existing));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}