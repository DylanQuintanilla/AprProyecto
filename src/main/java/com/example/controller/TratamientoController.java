package com.example.controller;

import com.example.controller.request.TratamientoRequest;
import com.example.controller.response.TratamientoResponse;
import com.example.service.TratamientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tratamientos")
public class TratamientoController {

    private final TratamientoService tratamientoService;

    @GetMapping
    public List<TratamientoResponse> getAll() {
        return tratamientoService.findAll();
    }

    @GetMapping("/{id}")
    public TratamientoResponse getById(@PathVariable Long id) {
        return tratamientoService.findById(id);
    }

    @PostMapping
    public TratamientoResponse create(@Valid @RequestBody TratamientoRequest request) {
        return tratamientoService.save(request);
    }

    @PutMapping("/{id}")
    public TratamientoResponse update(@PathVariable Long id, @Valid @RequestBody TratamientoRequest request) {
        return tratamientoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tratamientoService.deleteById(id);
    }
}