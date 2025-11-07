package com.example.service;

import com.example.controller.request.AntecedenteMedicoRequest;
import com.example.controller.response.AntecedenteMedicoResponse;
import com.example.controller.response.common.GenericResponse;
import java.util.List;

public interface AntecedenteMedicoService {
    List<AntecedenteMedicoResponse> findAll();
    AntecedenteMedicoResponse findById(Long id);
    AntecedenteMedicoResponse save(AntecedenteMedicoRequest request);
    AntecedenteMedicoResponse update(Long id, AntecedenteMedicoRequest request);
    GenericResponse deleteById(Long id);
}