package com.example.service.mapper;

import com.example.controller.request.DentistaRequest;
import com.example.controller.response.DentistaResponse;
import com.example.model.entity.Dentista;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DentistaMapper {
    // Ignoramos especialidades en el mapeo desde request → entity (se asignan manualmente en el servicio)
    @Mapping(target = "especialidades", ignore = true)
    Dentista toDentista(DentistaRequest request);

    DentistaResponse toDentistaResponse(Dentista dentista);
    List<DentistaResponse> toDentistaResponseList(List<Dentista> dentistas);
}