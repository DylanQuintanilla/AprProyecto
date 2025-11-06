package com.example.service.mapper;

import com.example.controller.request.TratamientoRequest;
import com.example.controller.response.TratamientoResponse;
import com.example.model.entity.Tratamiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TratamientoMapper {
    @Mapping(target = "enfermedad", ignore = true) // Se asignará manualmente en el servicio
    Tratamiento toTratamiento(TratamientoRequest request);

    TratamientoResponse toTratamientoResponse(Tratamiento tratamiento);
    List<TratamientoResponse> toTratamientoResponseList(List<Tratamiento> tratamientos);
}