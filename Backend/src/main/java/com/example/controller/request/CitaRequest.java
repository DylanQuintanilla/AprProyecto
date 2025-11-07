package com.example.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CitaRequest {



    @NotNull(message = "El ID del dentista es obligatorio")
    private Long dentistaId;

    private Long consultorioId;

    @NotNull(message = "La fecha de la cita es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaCita;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime hora;

    private String motivo;
    private String descripcion;

    @NotNull(message = "El estado de la cita es obligatorio")
    private Long estadoId;

    private Long tipoId;

    private List<Long> enfermedadesIds; // IDs de enfermedades asociadas
}