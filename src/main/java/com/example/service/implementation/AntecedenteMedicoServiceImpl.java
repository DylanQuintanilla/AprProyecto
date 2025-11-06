package com.example.service.implementation;

import com.example.controller.request.AntecedenteMedicoRequest;
import com.example.controller.response.AntecedenteMedicoResponse;
import com.example.model.entity.AntecedenteMedico;
import com.example.model.entity.Paciente;
import com.example.repository.AntecedenteMedicoRepository;
import com.example.repository.PacienteRepository;
import com.example.service.AntecedenteMedicoService;
import com.example.service.mapper.AntecedenteMedicoMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AntecedenteMedicoServiceImpl implements AntecedenteMedicoService {

    private final AntecedenteMedicoRepository repository;
    private final PacienteRepository pacienteRepository;
    private final AntecedenteMedicoMapper mapper;

    @Override
    public List<AntecedenteMedicoResponse> findAll() {
        return mapper.toAntecedenteMedicoResponseList(repository.findAll());
    }

    @Override
    public AntecedenteMedicoResponse findById(Long id) {
        return mapper.toAntecedenteMedicoResponse(
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Antecedente médico no encontrado"))
        );
    }

    @Override
    public AntecedenteMedicoResponse save(AntecedenteMedicoRequest request) {
        AntecedenteMedico antecedente = mapper.toAntecedenteMedico(request);
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        antecedente.setPaciente(paciente);
        return mapper.toAntecedenteMedicoResponse(repository.save(antecedente));
    }

    @Override
    public AntecedenteMedicoResponse update(Long id, AntecedenteMedicoRequest request) {
        AntecedenteMedico existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Antecedente médico no encontrado"));

        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        existing.setPaciente(paciente);
        existing.setDescripcion(request.getDescripcion());

        return mapper.toAntecedenteMedicoResponse(repository.save(existing));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}