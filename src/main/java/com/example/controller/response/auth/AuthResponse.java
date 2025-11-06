package com.example.controller.response.auth;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonPropertyOrder({"username", "message", "status", "accessToken", "refreshToken"})
public class AuthResponse {
    private String username;
    private String message;
    private String accessToken;  // <-- Renombrado de 'jwt'
    private String refreshToken; // <-- Nuevo campo
    private Boolean status;
}