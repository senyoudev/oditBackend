package com.oditbackend.authservice.service;


import com.oditbackend.authservice.Dto.AuthenticationRequest;
import com.oditbackend.authservice.Dto.AuthenticationResponse;
import com.oditbackend.authservice.Dto.RegisterRequest;
import com.oditbackend.authservice.entity.Role;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.AuthenticationException;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthenticationResponse.builder().token("").message("Email already exists.").build();
        }
        validateRegistrationRequest(request);
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.User)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);


        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Well Registered")
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("well loggedIn")
                .build();
    }


    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    private void validateRegistrationRequest(RegisterRequest request) {
        if (StringUtils.isBlank(request.getFirstName()) ||
                StringUtils.isBlank(request.getLastName()) ||
                StringUtils.isBlank(request.getEmail()) ||
                StringUtils.isBlank(request.getPassword())) {
            throw new IllegalArgumentException("All fields are required.");
        }

        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[a-zA-Z]{2,})$");
    }

}