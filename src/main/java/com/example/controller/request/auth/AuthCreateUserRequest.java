package com.example.controller.request.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthCreateUserRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Valid
    private AuthCreateRoleRequest roleRequest;
}