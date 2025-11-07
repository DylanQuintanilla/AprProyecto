package com.example.service;

import com.example.controller.request.PacienteRequest;
import com.example.controller.response.PacienteResponse;
import com.example.controller.response.common.GenericResponse;

import java.util.List;

public interface PacienteService {
    List<PacienteResponse> findAll();
    PacienteResponse findById(Long id);
    PacienteResponse save(PacienteRequest request);
    PacienteResponse update(Long id, PacienteRequest request);
    GenericResponse deleteById(Long id);
}