package com.example.service;

import com.example.controller.request.DentistaRequest;
import com.example.controller.response.DentistaResponse;
import java.util.List;

public interface DentistaService {
    List<DentistaResponse> findAll();
    DentistaResponse findById(Long id);
    DentistaResponse save(DentistaRequest request);
    DentistaResponse update(Long id, DentistaRequest request);
    void deleteById(Long id);
}