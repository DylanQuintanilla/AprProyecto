package com.example.controller;

import com.example.controller.request.DentistaRequest;
import com.example.controller.response.DentistaResponse;
import com.example.service.DentistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dentistas")
public class DentistaController {

    private final DentistaService dentistaService;

    @GetMapping
    public List<DentistaResponse> getAll() {
        return dentistaService.findAll();
    }

    @GetMapping("/{id}")
    public DentistaResponse getById(@PathVariable Long id) {
        return dentistaService.findById(id);
    }

    @PostMapping
    public DentistaResponse create(@Valid @RequestBody DentistaRequest request) {
        return dentistaService.save(request);
    }

    @PutMapping("/{id}")
    public DentistaResponse update(@PathVariable Long id, @Valid @RequestBody DentistaRequest request) {
        return dentistaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dentistaService.deleteById(id);
    }
}