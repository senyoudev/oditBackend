package com.oditbackend.authservice.controller;

import com.oditbackend.authservice.Dto.*;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> Profile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(service.getProfile(authorization));
    }

    @PutMapping(value = "/profile")
    public ResponseEntity<AuthenticationResponse> updateProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,@RequestBody ProfileUpdateRequest request){
        return ResponseEntity.ok(service.updateProfile(authorization,request));
    }

    @PutMapping(value = "/email")
    public ResponseEntity<Object> updateEmail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,@RequestBody EmailUpdateRequest request){
        return ResponseEntity.ok(service.updateEmail(authorization,request));
    }

    @PutMapping(value = "/password")
    public ResponseEntity<AuthenticationResponse> updatePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,@RequestBody PasswordUpdateRequest request){
        return ResponseEntity.ok(service.updatePassword(authorization,request));
    }
    @DeleteMapping
    public ResponseEntity<String> deleteAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){
        return  ResponseEntity.ok(service.deleteAccount(authorization));
    }
}