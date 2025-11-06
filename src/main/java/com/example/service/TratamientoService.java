package com.example.service;

import com.example.controller.request.TratamientoRequest;
import com.example.controller.response.TratamientoResponse;
import java.util.List;

public interface TratamientoService {
    List<TratamientoResponse> findAll();
    TratamientoResponse findById(Long id);
    TratamientoResponse save(TratamientoRequest request);
    TratamientoResponse update(Long id, TratamientoRequest request);
    void deleteById(Long id);
}