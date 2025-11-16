package com.personal.ecommerceauth.controller;


import com.personal.ecommerceauth.dto.request.LoginRequest;
import com.personal.ecommerceauth.dto.request.RegisterRequest;
import com.personal.ecommerceauth.dto.response.LoginResponse;
import com.personal.ecommerceauth.dto.response.RegisterResponse;
import com.personal.ecommerceauth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registerUser")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.created(URI.create("/auth/registerUser"))
                             .body(authService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
