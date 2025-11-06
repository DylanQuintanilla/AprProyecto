package com.example.service;

import com.example.controller.request.ConsultorioRequest;
import com.example.controller.response.ConsultorioResponse;
import java.util.List;

public interface ConsultorioService {
    List<ConsultorioResponse> findAll();
    ConsultorioResponse findById(Long id);
    ConsultorioResponse save(ConsultorioRequest request);
    ConsultorioResponse update(Long id, ConsultorioRequest request);
    void deleteById(Long id);
}