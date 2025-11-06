package com.example.controller;

import com.example.controller.request.EnfermedadRequest;
import com.example.controller.response.EnfermedadResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.service.EnfermedadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enfermedades")
public class EnfermedadController {

    private final EnfermedadService enfermedadService;

    @GetMapping
    public List<EnfermedadResponse> getAll() {
        return enfermedadService.findAll();
    }

    @GetMapping("/{id}")
    public EnfermedadResponse getById(@PathVariable Long id) {
        return enfermedadService.findById(id);
    }

    @PostMapping
    public EnfermedadResponse create(@Valid @RequestBody EnfermedadRequest request) {
        return enfermedadService.save(request);
    }

    @PutMapping("/{id}")
    public EnfermedadResponse update(@PathVariable Long id, @Valid @RequestBody EnfermedadRequest request) {
        return enfermedadService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public GenericResponse delete(@PathVariable Long id) {
        return enfermedadService.deleteById(id);
    }
}