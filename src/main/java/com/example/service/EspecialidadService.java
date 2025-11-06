package com.example.service;

import com.example.controller.request.EspecialidadRequest;
import com.example.controller.response.EspecialidadResponse;
import com.example.controller.response.common.GenericResponse;

import java.util.List;

public interface EspecialidadService {
    List<EspecialidadResponse> findAll();
    EspecialidadResponse findById(Long id);
    EspecialidadResponse save(EspecialidadRequest request);
    EspecialidadResponse update(Long id, EspecialidadRequest request);
    GenericResponse deleteById(Long id);
}