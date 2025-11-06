package com.example.service.mapper;

import com.example.controller.request.TipoCitaRequest;
import com.example.controller.response.TipoCitaResponse;
import com.example.model.entity.TipoCita;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TipoCitaMapper {
    TipoCita toTipoCita(TipoCitaRequest request);
    TipoCitaResponse toTipoCitaResponse(TipoCita tipoCita);
    List<TipoCitaResponse> toTipoCitaResponseList(List<TipoCita> tipos);
}
