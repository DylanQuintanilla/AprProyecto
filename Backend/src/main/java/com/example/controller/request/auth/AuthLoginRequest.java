package com.example.controller.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthLoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
