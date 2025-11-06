package com.example.service.mapper;

import com.example.controller.request.AntecedenteMedicoRequest;
import com.example.controller.response.AntecedenteMedicoResponse;
import com.example.model.entity.AntecedenteMedico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AntecedenteMedicoMapper {
    @Mapping(target = "paciente", ignore = true) // Se asigna en el servicio
    AntecedenteMedico toAntecedenteMedico(AntecedenteMedicoRequest request);

    AntecedenteMedicoResponse toAntecedenteMedicoResponse(AntecedenteMedico antecedente);
    List<AntecedenteMedicoResponse> toAntecedenteMedicoResponseList(List<AntecedenteMedico> antecedentes);
}