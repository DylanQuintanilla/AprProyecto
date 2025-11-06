package com.example.controller;

import com.example.controller.request.TipoCitaRequest;
import com.example.controller.response.TipoCitaResponse;
import com.example.controller.response.common.GenericResponse;
import com.example.service.TipoCitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tipo-citas")
public class TipoCitaController {

    private final TipoCitaService tipoCitaService;

    @GetMapping
    public List<TipoCitaResponse> getAll() {
        return tipoCitaService.findAll();
    }

    @GetMapping("/{id}")
    public TipoCitaResponse getById(@PathVariable Long id) {
        return tipoCitaService.findById(id);
    }

    @PostMapping
    public TipoCitaResponse create(@Valid @RequestBody TipoCitaRequest request) {
        return tipoCitaService.save(request);
    }

    @PutMapping("/{id}")
    public TipoCitaResponse update(@PathVariable Long id, @Valid @RequestBody TipoCitaRequest request) {
        return tipoCitaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public GenericResponse delete(@PathVariable Long id) {
        return tipoCitaService.deleteById(id);
    }
}