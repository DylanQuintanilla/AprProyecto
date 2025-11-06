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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional // <-- Ya debió ser añadido antes
    public CitaResponse save(CitaRequest request) {
        // 1. Obtener el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long pacienteIdUsar;
        Paciente pacienteSolicitante;

        // --- Lógica de Seguridad (IDOR Prevention) ---
        if (hasAuthority(authentication, "citas:solicitar:propias")) {
            // ES PACIENTE: Obtenemos su ID del contexto de seguridad y lo forzamos.
            pacienteSolicitante = pacienteRepository.findByUserUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado para el usuario: " + username));
            pacienteIdUsar = pacienteSolicitante.getId();

        } else if (hasAuthority(authentication, "citas:gestionar:asignadas") || hasAuthority(authentication, "citas:admin")) {

            // Es DOCTOR/ADMIN: ESTE ENDPOINT NO DEBERÍA PERMITIRLES CREAR CITAS.
            // La creación de citas por parte del personal médico suele ser a través de un proceso
            // diferente que sí requiere el pacienteId en el DTO (ej. un DTO diferente).
            // Al haber quitado el pacienteId de CitaRequest, forzamos que solo el paciente pueda usar este POST.
            throw new AccessDeniedException("Solo los pacientes pueden solicitar citas por esta vía.");

        } else {
            throw new AccessDeniedException("No tiene permisos para crear esta cita");
        }

        // --- Continuar el proceso de guardado ---
        Cita cita = mapper.toCita(request);

        // Asignamos el ID del Paciente OBTENIDO DEL CONTEXTO SE SEGURIDAD (IDOR Prevención)
        cita.setPaciente(pacienteSolicitante); // Usamos la entidad que ya encontramos

        // El resto de la lógica sigue igual
        cita.setDentista(getDentista(request.getDentistaId()));
        cita.setConsultorio(getConsultorio(request.getConsultorioId()));
        cita.setEstado(getEstado(request.getEstadoId()));
        cita.setTipo(getTipo(request.getTipoId()));
        cita.setEnfermedades(getEnfermedades(request.getEnfermedadesIds()));
        cita.setFechaCita(LocalDateTime.of(request.getFechaCita(), request.getHora() != null ? request.getHora() : java.time.LocalTime.MIDNIGHT));
        cita.setMotivo(request.getMotivo());
        cita.setDescripcion(request.getDescripcion());

        return mapper.toCitaResponse(repository.save(cita));
    }

    @Override
    @Transactional // <-- Ya debió ser añadido
    public CitaResponse update(Long id, CitaRequest request) {
        // ... (Tu lógica de seguridad para DOCTOR/ADMIN) ...

        Cita existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        // --- CAMBIO CLAVE AQUÍ ---
        // NO usamos getPaciente(request.getPacienteId()) porque lo eliminamos del DTO.
        // Mantenemos el paciente existente.
        // existing.setPaciente(getPaciente(request.getPacienteId())); // <-- ELIMINAR O COMENTAR ESTO

        existing.setDentista(getDentista(request.getDentistaId()));
        existing.setConsultorio(getConsultorio(request.getConsultorioId()));
        // ... (resto de las asignaciones)

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