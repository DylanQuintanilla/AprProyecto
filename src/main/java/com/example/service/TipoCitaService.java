package com.example.service;

import com.example.controller.request.TipoCitaRequest;
import com.example.controller.response.TipoCitaResponse;
import java.util.List;

public interface TipoCitaService {
    List<TipoCitaResponse> findAll();
    TipoCitaResponse findById(Long id);
    TipoCitaResponse save(TipoCitaRequest request);
    TipoCitaResponse update(Long id, TipoCitaRequest request);
    void deleteById(Long id);
}