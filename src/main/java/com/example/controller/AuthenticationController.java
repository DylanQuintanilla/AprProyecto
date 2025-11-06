package com.example.controller;

import com.example.controller.request.auth.AuthCreateUserRequest;
import com.example.controller.request.auth.AuthLoginRequest;
import com.example.controller.response.auth.AuthResponse;
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

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthCreateUserRequest userRequest){
        return this.userDetailService.createUser(userRequest);
    }

    @PostMapping("/log-in")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest userRequest){
        return this.userDetailService.loginUser(userRequest);
    }
}