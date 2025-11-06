package com.example.controller;

import com.example.controller.request.PacienteRequest;
import com.example.controller.response.PacienteResponse;
import com.example.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    public List<PacienteResponse> getAll() {
        return pacienteService.findAll();
    }

    @GetMapping("/{id}")
    public PacienteResponse getById(@PathVariable Long id) {
        return pacienteService.findById(id);
    }

    @PostMapping
    public PacienteResponse create(@Valid @RequestBody PacienteRequest request) {
        return pacienteService.save(request);
    }

    @PutMapping("/{id}")
    public PacienteResponse update(@PathVariable Long id, @Valid @RequestBody PacienteRequest request) {
        return pacienteService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pacienteService.deleteById(id);
    }
}