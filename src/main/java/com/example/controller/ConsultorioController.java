package com.example.controller;

import com.example.controller.request.ConsultorioRequest;
import com.example.controller.response.ConsultorioResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.service.ConsultorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consultorios")
public class ConsultorioController {

    private final ConsultorioService consultorioService;

    @GetMapping
    public List<ConsultorioResponse> getAll() {
        return consultorioService.findAll();
    }

    @GetMapping("/{id}")
    public ConsultorioResponse getById(@PathVariable Long id) {
        return consultorioService.findById(id);
    }

    @PostMapping
    public ConsultorioResponse create(@Valid @RequestBody ConsultorioRequest request) {
        return consultorioService.save(request);
    }

    @PutMapping("/{id}")
    public ConsultorioResponse update(@PathVariable Long id, @Valid @RequestBody ConsultorioRequest request) {
        return consultorioService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public GenericResponse delete(@PathVariable Long id) {
        return consultorioService.deleteById(id);
    }
}