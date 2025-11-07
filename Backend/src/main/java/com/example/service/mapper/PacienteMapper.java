package com.example.service.mapper;

import com.example.controller.request.PacienteRequest;
import com.example.controller.response.PacienteResponse;
import com.example.model.entity.Paciente;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PacienteMapper {
    Paciente toPaciente(PacienteRequest request);
    PacienteResponse toPacienteResponse(Paciente paciente);
    List<PacienteResponse> toPacienteResponseList(List<Paciente> pacientes);
}