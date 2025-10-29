package com.api.pos_backend.controller;

import com.api.pos_backend.records.AuthCreateUserRequest;
import com.api.pos_backend.records.AuthLoginRequest;
import com.api.pos_backend.records.AuthResponse;
import com.api.pos_backend.service.implement.AuthenticationUserServiceDetailsImplement;
import com.api.pos_backend.shared.authentication.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationUserServiceDetailsImplement serviceDetailsImplement;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLoginRequest authLoginRequest) {
        AuthResponse authResponse = serviceDetailsImplement.login(authLoginRequest);
        log.info("Usuario {} ha iniciado sesión.", authLoginRequest.username());
        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthCreateUserRequest authCreateUserRequest) {
        AuthResponse authResponse = serviceDetailsImplement.createUser(authCreateUserRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verifyToken(@RequestHeader String token) {
        AuthResponse authResponse = serviceDetailsImplement.verifyToken(token);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        var response = serviceDetailsImplement.logout(authHeader);
        log.info("Usuario ha cerrado sesión.");
        return ResponseEntity.ok(response);
    }
}
