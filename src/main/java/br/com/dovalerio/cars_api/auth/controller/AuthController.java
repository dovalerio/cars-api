package br.com.dovalerio.cars_api.auth.controller;

import br.com.dovalerio.cars_api.auth.dto.LoginRequest;
import br.com.dovalerio.cars_api.auth.dto.TokenResponse;
import br.com.dovalerio.cars_api.auth.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}