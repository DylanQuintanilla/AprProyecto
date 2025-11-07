package com.example.service.mapper;

import com.example.controller.request.EspecialidadRequest;
import com.example.controller.response.EspecialidadResponse;
import com.example.model.entity.Especialidad;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EspecialidadMapper {
    Especialidad toEspecialidad(EspecialidadRequest request);
    EspecialidadResponse toEspecialidadResponse(Especialidad especialidad);
    List<EspecialidadResponse> toEspecialidadResponseList(List<Especialidad> especialidades);
}