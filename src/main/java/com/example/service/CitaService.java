package com.example.service;

import com.example.controller.request.CitaRequest;
import com.example.controller.response.CitaResponse;
import java.util.List;

public interface CitaService {
    List<CitaResponse> findAll();
    CitaResponse findById(Long id);
    CitaResponse save(CitaRequest request);
    CitaResponse update(Long id, CitaRequest request);
    void deleteById(Long id);
}