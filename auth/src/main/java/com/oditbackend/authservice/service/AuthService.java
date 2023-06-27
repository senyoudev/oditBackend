package com.oditbackend.authservice.service;


import com.oditbackend.authservice.entity.Token;
import com.oditbackend.authservice.entity.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oditbackend.authservice.Dto.AuthenticationRequest;
import com.oditbackend.authservice.Dto.AuthenticationResponse;
import com.oditbackend.authservice.Dto.RegisterRequest;
import com.oditbackend.authservice.entity.Role;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.repository.TokenRepository;
import com.oditbackend.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final  UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthenticationResponse.builder().accessToken("").message("Email already exists.").build();
        }
        validateRegistrationRequest(request);
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.User)
                .build();
        User savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);


        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
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
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .message("Well Logged In")
                .build();
    }


    public Integer validateToken(String token) {
        jwtService.validateToken(token);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalStateException("user with email {email} does not exist"));
        return user.getId();
    }

    public boolean isTokenValidAsAdmin(String token) {
        if (token != null) {
            try {
                jwtService.validateToken(token);
                String email = jwtService.extractUsername(token);

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalStateException("User with email " + email + " does not exist"));

                return user.getRole() == Role.Admin;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
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

    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if(validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
           token.setRevoked(true);
        });
       tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

}