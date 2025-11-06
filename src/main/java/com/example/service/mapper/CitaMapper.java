package com.example.service.mapper;

import com.example.controller.request.CitaRequest;
import com.example.controller.response.CitaResponse;
import com.example.model.entity.Cita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CitaMapper {
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "dentista", ignore = true)
    @Mapping(target = "consultorio", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(target = "enfermedades", ignore = true) // Se asignan manualmente en el servicio
    Cita toCita(CitaRequest request);

    CitaResponse toCitaResponse(Cita cita);
    List<CitaResponse> toCitaResponseList(List<Cita> citas);
}