package com.example.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

@Getter
@Setter
@Builder(toBuilder = true)
@FieldNameConstants
public class PacienteResponse {
    private Long id;
    private String nombre;
    private String apellido;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date fechaNacimiento;
    private String numeroTelefono;
    private String email;
    private String usuario;
    private String dui;
    // No se incluye la contraseña en la respuesta
}