package com.example.controller;

import com.example.controller.request.AntecedenteMedicoRequest;
import com.example.controller.response.AntecedenteMedicoResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.service.AntecedenteMedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/antecedentes-medicos")
public class AntecedenteMedicoController {

    private final AntecedenteMedicoService antecedenteMedicoService;

    @GetMapping
    public List<AntecedenteMedicoResponse> getAll() {
        return antecedenteMedicoService.findAll();
    }

    @GetMapping("/{id}")
    public AntecedenteMedicoResponse getById(@PathVariable Long id) {
        return antecedenteMedicoService.findById(id);
    }

    @PostMapping
    public AntecedenteMedicoResponse create(@Valid @RequestBody AntecedenteMedicoRequest request) {
        return antecedenteMedicoService.save(request);
    }

    @PutMapping("/{id}")
    public AntecedenteMedicoResponse update(@PathVariable Long id, @Valid @RequestBody AntecedenteMedicoRequest request) {
        return antecedenteMedicoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public GenericResponse delete(@PathVariable Long id) {
        return antecedenteMedicoService.deleteById(id);
    }
}