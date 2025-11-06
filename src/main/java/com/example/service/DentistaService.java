package com.example.service;

import com.example.controller.request.DentistaRequest;
import com.example.controller.response.DentistaResponse;
import com.example.controller.response.common.GenericResponse;

import java.util.List;

public interface DentistaService {
    List<DentistaResponse> findAll();
    DentistaResponse findById(Long id);
    DentistaResponse save(DentistaRequest request);
    DentistaResponse update(Long id, DentistaRequest request);
    GenericResponse deleteById(Long id);
}