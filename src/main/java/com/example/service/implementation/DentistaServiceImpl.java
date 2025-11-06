package com.example.service.implementation;

import com.example.controller.request.DentistaRequest;
import com.example.controller.response.DentistaResponse;
import com.example.model.entity.Dentista;
import com.example.model.entity.Especialidad;
import com.example.repository.DentistaRepository;
import com.example.repository.EspecialidadRepository;
import com.example.service.DentistaService;
import com.example.service.mapper.DentistaMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        return mapper.toDentistaResponse(
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado"))
        );
    }

    @Override
    public DentistaResponse save(DentistaRequest request) {
        if (repository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ValidationException("Ya existe un dentista con ese correo");
        }
        if (repository.existsByUsuarioIgnoreCase(request.getUsuario())) {
            throw new ValidationException("Ya existe un dentista con ese nombre de usuario");
        }

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
        Dentista existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado"));

        if (!existing.getEmail().equalsIgnoreCase(request.getEmail()) &&
                repository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ValidationException("Ya existe un dentista con ese correo");
        }
        if (request.getUsuario() != null && !existing.getUsuario().equalsIgnoreCase(request.getUsuario()) &&
                repository.existsByUsuarioIgnoreCase(request.getUsuario())) {
            throw new ValidationException("Ya existe un dentista con ese nombre de usuario");
        }

        existing.setNombre(request.getNombre());
        existing.setApellido(request.getApellido());
        existing.setEmail(request.getEmail());
        existing.setUsuario(request.getUsuario());

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
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}