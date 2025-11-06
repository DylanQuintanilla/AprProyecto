package com.example.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@Builder(toBuilder = true)
@FieldNameConstants
public class ConsultorioResponse {
    private Long id;
    private String nombre;
    private String ubicacion;
}