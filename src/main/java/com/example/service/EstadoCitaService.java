package com.example.service;

import com.example.controller.request.EstadoCitaRequest;
import com.example.controller.response.EstadoCitaResponse;
import java.util.List;

public interface EstadoCitaService {
    List<EstadoCitaResponse> findAll();
    EstadoCitaResponse findById(Long id);
    EstadoCitaResponse save(EstadoCitaRequest request);
    EstadoCitaResponse update(Long id, EstadoCitaRequest request);
    void deleteById(Long id);
}