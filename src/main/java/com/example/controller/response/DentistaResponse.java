package com.example.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@FieldNameConstants
public class DentistaResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String usuario;
    private String email;
    private List<EspecialidadResponse> especialidades;
}