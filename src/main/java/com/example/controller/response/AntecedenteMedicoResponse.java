package com.example.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@FieldNameConstants
public class AntecedenteMedicoResponse {
    private Long id;
    private PacienteResponse paciente;
    private String descripcion;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime fechaRegistro;
}