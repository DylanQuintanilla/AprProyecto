package com.example.controller;

import com.example.controller.request.CitaRequest;
import com.example.controller.response.CitaResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;

    @GetMapping
    public List<CitaResponse> getAll() {
        return citaService.findAll();
    }

    @GetMapping("/{id}")
    public CitaResponse getById(@PathVariable Long id) {
        return citaService.findById(id);
    }

    @PostMapping
    public CitaResponse create(@Valid @RequestBody CitaRequest request) {
        return citaService.save(request);
    }

    @PutMapping("/{id}")
    public CitaResponse update(@PathVariable Long id, @Valid @RequestBody CitaRequest request) {
        return citaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public GenericResponse delete(@PathVariable Long id) {
        return citaService.deleteById(id);
    }
}