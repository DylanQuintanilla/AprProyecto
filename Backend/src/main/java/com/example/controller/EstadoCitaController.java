package com.example.controller;

import com.example.controller.request.EstadoCitaRequest;
import com.example.controller.response.EstadoCitaResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.service.EstadoCitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/estado-citas")
public class EstadoCitaController {

    private final EstadoCitaService estadoCitaService;

    @GetMapping
    public List<EstadoCitaResponse> getAll() {
        return estadoCitaService.findAll();
    }

    @GetMapping("/{id}")
    public EstadoCitaResponse getById(@PathVariable Long id) {
        return estadoCitaService.findById(id);
    }

    @PostMapping
    public EstadoCitaResponse create(@Valid @RequestBody EstadoCitaRequest request) {
        return estadoCitaService.save(request);
    }

    @PutMapping("/{id}")
    public EstadoCitaResponse update(@PathVariable Long id, @Valid @RequestBody EstadoCitaRequest request) {
        return estadoCitaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public GenericResponse delete(@PathVariable Long id) {
        return estadoCitaService.deleteById(id);
    }
}