package com.example.service.mapper;

import com.example.controller.request.EstadoCitaRequest;
import com.example.controller.response.EstadoCitaResponse;
import com.example.model.entity.EstadoCita;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstadoCitaMapper {
    EstadoCita toEstadoCita(EstadoCitaRequest request);
    EstadoCitaResponse toEstadoCitaResponse(EstadoCita estadoCita);
    List<EstadoCitaResponse> toEstadoCitaResponseList(List<EstadoCita> estados);
}