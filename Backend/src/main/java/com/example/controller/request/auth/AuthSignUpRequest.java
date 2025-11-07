// RUTA: src/main/java/com/example/controller/request/auth/AuthSignUpRequest.java
package com.example.controller.request.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class AuthSignUpRequest {

    // Datos de Usuario
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6)
    private String password;

    // Datos de Paciente
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100)
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date fechaNacimiento;

    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 254)
    private String email;

    @NotBlank(message = "El DUI es obligatorio")
    @Pattern(regexp = "^\\d{8}-\\d{1}$", message = "El DUI debe tener el formato 12345678-9")
    private String dui;

    @Size(max = 20)
    private String numeroTelefono;
}