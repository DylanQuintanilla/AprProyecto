package com.example.service.implementation;

import com.example.controller.request.CitaRequest;
import com.example.controller.response.CitaResponse;
import com.example.model.entity.*;
import com.example.repository.*;
import com.example.service.CitaService;
import com.example.service.mapper.CitaMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository repository;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;
    private final ConsultorioRepository consultorioRepository;
    private final EstadoCitaRepository estadoCitaRepository;
    private final TipoCitaRepository tipoCitaRepository;
    private final EnfermedadRepository enfermedadRepository;
    private final CitaMapper mapper;

    @Override
    public List<CitaResponse> findAll() {
        return mapper.toCitaResponseList(repository.findAll());
    }

    @Override
    public CitaResponse findById(Long id) {
        return mapper.toCitaResponse(
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"))
        );
    }

    @Override
    public CitaResponse save(CitaRequest request) {
        Cita cita = mapper.toCita(request);
        cita.setPaciente(getPaciente(request.getPacienteId()));
        cita.setDentista(getDentista(request.getDentistaId()));
        cita.setConsultorio(getConsultorio(request.getConsultorioId()));
        cita.setEstado(getEstado(request.getEstadoId()));
        cita.setTipo(getTipo(request.getTipoId()));
        cita.setEnfermedades(getEnfermedades(request.getEnfermedadesIds()));
        cita.setFechaCita(LocalDateTime.of(request.getFechaCita(), request.getHora() != null ? request.getHora() : java.time.LocalTime.MIDNIGHT));
        return mapper.toCitaResponse(repository.save(cita));
    }

    @Override
    public CitaResponse update(Long id, CitaRequest request) {
        Cita existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        existing.setPaciente(getPaciente(request.getPacienteId()));
        existing.setDentista(getDentista(request.getDentistaId()));
        existing.setConsultorio(getConsultorio(request.getConsultorioId()));
        existing.setEstado(getEstado(request.getEstadoId()));
        existing.setTipo(getTipo(request.getTipoId()));
        existing.setEnfermedades(getEnfermedades(request.getEnfermedadesIds()));
        existing.setFechaCita(LocalDateTime.of(request.getFechaCita(), request.getHora() != null ? request.getHora() : java.time.LocalTime.MIDNIGHT));
        existing.setMotivo(request.getMotivo());
        existing.setDescripcion(request.getDescripcion());

        return mapper.toCitaResponse(repository.save(existing));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private Paciente getPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
    }

    private Dentista getDentista(Long id) {
        return dentistaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado"));
    }

    private Consultorio getConsultorio(Long id) {
        if (id == null) return null;
        return consultorioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultorio no encontrado"));
    }

    private EstadoCita getEstado(Long id) {
        return estadoCitaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estado de cita no encontrado"));
    }

    private TipoCita getTipo(Long id) {
        if (id == null) return null;
        return tipoCitaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de cita no encontrado"));
    }

    private List<Enfermedad> getEnfermedades(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        List<Enfermedad> enfermedades = enfermedadRepository.findAllById(ids);
        if (enfermedades.size() != ids.size()) {
            throw new EntityNotFoundException("Una o más enfermedades no existen");
        }
        return enfermedades;
    }
}