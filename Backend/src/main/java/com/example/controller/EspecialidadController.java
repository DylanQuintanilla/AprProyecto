package com.example.controller;

import com.example.controller.request.EspecialidadRequest;
import com.example.controller.response.EspecialidadResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.service.EspecialidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/especialidades")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @GetMapping
    public List<EspecialidadResponse> getAll() {
        return especialidadService.findAll();
    }

    @GetMapping("/{id}")
    public EspecialidadResponse getById(@PathVariable Long id) {
        return especialidadService.findById(id);
    }

    @PostMapping
    public EspecialidadResponse create(@Valid @RequestBody EspecialidadRequest request) {
        return especialidadService.save(request);
    }

    @PutMapping("/{id}")
    public EspecialidadResponse update(@PathVariable Long id, @Valid @RequestBody EspecialidadRequest request) {
        return especialidadService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public GenericResponse delete(@PathVariable Long id) {
        return especialidadService.deleteById(id);
    }
}