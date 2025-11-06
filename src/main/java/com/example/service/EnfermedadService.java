package com.example.service;

import com.example.controller.request.EnfermedadRequest;
import com.example.controller.response.EnfermedadResponse;
import com.example.controller.response.common.GenericResponse;

import java.util.List;

public interface EnfermedadService {
    List<EnfermedadResponse> findAll();
    EnfermedadResponse findById(Long id);
    EnfermedadResponse save(EnfermedadRequest request);
    EnfermedadResponse update(Long id, EnfermedadRequest request);
    GenericResponse deleteById(Long id);
}