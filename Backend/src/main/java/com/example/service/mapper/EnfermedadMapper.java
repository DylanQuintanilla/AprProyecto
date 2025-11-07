package com.example.service.mapper;

import com.example.controller.request.EnfermedadRequest;
import com.example.controller.response.EnfermedadResponse;
import com.example.model.entity.Enfermedad;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnfermedadMapper {
    Enfermedad toEnfermedad(EnfermedadRequest request);
    EnfermedadResponse toEnfermedadResponse(Enfermedad enfermedad);
    List<EnfermedadResponse> toEnfermedadResponseList(List<Enfermedad> enfermedades);
}