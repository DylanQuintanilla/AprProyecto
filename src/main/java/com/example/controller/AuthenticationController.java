package com.example.controller;

import com.example.controller.request.auth.AuthCreateUserRequest;
import com.example.controller.request.auth.AuthLoginRequest;
import com.example.controller.request.auth.RefreshRequest; // <-- 1. Importar
import com.example.controller.response.auth.AuthResponse;
import com.example.controller.response.auth.RefreshResponse; // <-- 2. Importar
import com.example.service.RefreshTokenService; // <-- 3. Importar
import com.example.service.implementation.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private RefreshTokenService refreshTokenService; // <-- 4. Inyectar

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthCreateUserRequest userRequest){
        return this.userDetailService.createUser(userRequest);
    }

    @PostMapping("/log-in")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest userRequest){
        return this.userDetailService.loginUser(userRequest);
    }

    // --- 5. AÑADIR ESTE NUEVO MÉTODO ---
    @PostMapping("/refresh")
    public RefreshResponse refreshAccessToken(@Valid @RequestBody RefreshRequest request) {
        return refreshTokenService.refreshAccessToken(request);
    }
}