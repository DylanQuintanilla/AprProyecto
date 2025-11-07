package com.example.service.mapper;

import com.example.controller.request.ConsultorioRequest;
import com.example.controller.response.ConsultorioResponse;
import com.example.model.entity.Consultorio;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsultorioMapper {
    Consultorio toConsultorio(ConsultorioRequest request);
    ConsultorioResponse toConsultorioResponse(Consultorio consultorio);
    List<ConsultorioResponse> toConsultorioResponseList(List<Consultorio> consultorios);
}