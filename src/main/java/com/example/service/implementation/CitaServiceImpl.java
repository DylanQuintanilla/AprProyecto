package com.example.service.implementation;

import com.example.controller.request.CitaRequest;
import com.example.controller.response.CitaResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.model.entity.*;
import com.example.repository.*;
import com.example.service.CitaService;
import com.example.service.mapper.CitaMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        // 1. Obtener la autenticación del usuario logueado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 2. Comprobar permisos y filtrar
        if (hasAuthority(authentication, "citas:admin")) {
            // Es ADMIN: devolver todo
            return mapper.toCitaResponseList(repository.findAll());

        } else if (hasAuthority(authentication, "citas:leer:asignadas")) {
            // Es DOCTOR: buscar su ID de dentista y filtrar
            Dentista dentista = dentistaRepository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado para el usuario: " + username));
            return mapper.toCitaResponseList(repository.findByDentistaId(dentista.getId()));

        } else if (hasAuthority(authentication, "citas:leer:propias")) {
            // Es PACIENTE: buscar su ID de paciente y filtrar
            Paciente paciente = pacienteRepository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado para el usuario: " + username));
            return mapper.toCitaResponseList(repository.findByPacienteId(paciente.getId()));
        }

        // Si no tiene ningún permiso, lanzar error
        throw new AccessDeniedException("No tiene permisos para ver esta información");
    }

    @Override
    public CitaResponse findById(Long id) {
        // (Por ahora, dejamos findById simple. Se puede añadir lógica de seguridad similar a findAll)
        Cita cita = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        // --- LÓGICA DE SEGURIDAD BÁSICA PARA findById ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (hasAuthority(authentication, "citas:admin")) {
            return mapper.toCitaResponse(cita); // Admin puede ver todo
        }

        if (hasAuthority(authentication, "citas:leer:asignadas")) {
            Dentista dentista = dentistaRepository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado"));
            if (!cita.getDentista().getId().equals(dentista.getId())) {
                throw new AccessDeniedException("No tiene permisos para ver esta cita");
            }
        }

        if (hasAuthority(authentication, "citas:leer:propias")) {
            Paciente paciente = pacienteRepository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
            if (!cita.getPaciente().getId().equals(paciente.getId())) {
                throw new AccessDeniedException("No tiene permisos para ver esta cita");
            }
        }
        // --- FIN DE LÓGICA DE SEGURIDAD ---

        return mapper.toCitaResponse(cita);
    }

    @Override
    public CitaResponse save(CitaRequest request) {
        // --- LÓGICA DE SEGURIDAD PARA CREAR ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (hasAuthority(authentication, "citas:solicitar:propias")) {
            // Es PACIENTE: verificar que está creando una cita para sí mismo
            Paciente paciente = pacienteRepository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
            if (!paciente.getId().equals(request.getPacienteId())) {
                throw new AccessDeniedException("No puede crear citas para otros pacientes");
            }
            // Aquí se podría forzar el estado a "PENDIENTE" si fuera necesario

        } else if (hasAuthority(authentication, "citas:gestionar:asignadas")) {
            // Es DOCTOR: verificar que está asignándose la cita a sí mismo
            Dentista dentista = dentistaRepository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado"));
            if (!dentista.getId().equals(request.getDentistaId())) {
                throw new AccessDeniedException("No puede crear citas para otros dentistas");
            }
        } else if (!hasAuthority(authentication, "citas:admin")) {
            // Si no es PACIENTE, DOCTOR (con los permisos correctos) o ADMIN, no puede crear
            throw new AccessDeniedException("No tiene permisos para crear esta cita");
        }
        // Si es ADMIN, puede crear cualquier cita (no se necesita check)

        // --- FIN DE LÓGICA DE SEGURIDAD ---

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
        // (Deberíamos añadir lógica de seguridad similar a save() aquí también)

        Cita existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        // --- LÓGICA DE SEGURIDAD PARA ACTUALIZAR (EJEMPLO) ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (hasAuthority(authentication, "citas:gestionar:asignadas")) {
            // Es DOCTOR: verificar que la cita le pertenece
            Dentista dentista = dentistaRepository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Dentista no encontrado"));
            if (!existing.getDentista().getId().equals(dentista.getId())) {
                throw new AccessDeniedException("No puede modificar una cita que no le pertenece");
            }
        } else if (!hasAuthority(authentication, "citas:admin")) {
            // Si no es DOCTOR o ADMIN, no puede actualizar
            throw new AccessDeniedException("No tiene permisos para modificar esta cita");
        }
        // --- FIN DE LÓGICA DE SEGURIDAD ---

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
    public GenericResponse deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Cita no encontrada con ID: " + id);
        }
        repository.deleteById(id);
        return new GenericResponse("Cita con ID " + id + " eliminada exitosamente.");
    }

    // --- Métodos de ayuda ---

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(authority));
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