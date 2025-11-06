package com.example.controller.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {

    @NotBlank(message = "El token de refresco no puede estar vacío")
    private String refreshToken;
}