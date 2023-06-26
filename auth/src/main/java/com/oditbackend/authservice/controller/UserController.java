package com.oditbackend.authservice.controller;

import com.oditbackend.authservice.Dto.*;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.service.AuthService;
import com.oditbackend.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping(value = "{id}")
    public ResponseEntity<User> getProfile(@PathVariable("id") Integer id) {
        return service.getProfile(id);
    }

    @PutMapping(value = "/profile/{id}")
    public ResponseEntity<AuthenticationResponse> updateProfile(@PathVariable("id") Integer id,@RequestBody ProfileUpdateRequest request){
        return service.updateProfile(id,request);
    }

    @PutMapping(value = "/email/{id}")
    public ResponseEntity<AuthenticationResponse> updateEmail(@PathVariable("id") Integer id,@RequestBody EmailUpdateRequest request){
        return service.updateEmail(id,request);
    }

    @PutMapping(value = "/password/{id}")
    public ResponseEntity<AuthenticationResponse> updatePassword(@PathVariable("id") Integer id,@RequestBody PasswordUpdateRequest request){
        return service.updatePassword(id,request);
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable("id") Integer id){
        return service.deleteAccount(id);
    }
}