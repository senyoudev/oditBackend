package com.oditbackend.authservice.controller;

import com.oditbackend.authservice.Dto.*;
import com.oditbackend.authservice.service.AuthService;
import com.oditbackend.authservice.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    private final PasswordResetService resetService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }


    @GetMapping("/validate")
    public TokenValidationResponse validateToken(@RequestParam("token") String token) {
        return service.validateToken(token);
    }
    @GetMapping("/validate-admin")
    public Boolean isTokenValidAsAdmin(@RequestParam("token") String token) {
        return service.isTokenValidAsAdmin(token);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

    @PostMapping("/reset-password/initiate")
    public ResponseEntity<String> initiatePasswordReset(@RequestBody PasswordResetRequest request) {
        resetService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Password reset initiated successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordReset request) {
        resetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully.");
    }
}