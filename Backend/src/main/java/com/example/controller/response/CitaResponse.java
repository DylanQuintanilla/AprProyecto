package com.example.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@FieldNameConstants
public class CitaResponse {
    private Long id;
    private PacienteResponse paciente;
    private DentistaResponse dentista;
    private ConsultorioResponse consultorio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCita;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime hora;
    private String motivo;
    private String descripcion;
    private EstadoCitaResponse estado;
    private TipoCitaResponse tipo;
    private List<EnfermedadResponse> enfermedades;
}