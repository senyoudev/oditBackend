package com.oditbackend.authservice.service;


import ch.qos.logback.core.encoder.EchoEncoder;
import com.oditbackend.authservice.Dto.AuthenticationRequest;
import com.oditbackend.authservice.Dto.AuthenticationResponse;
import com.oditbackend.authservice.Dto.RegisterRequest;
import com.oditbackend.authservice.entity.Role;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
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
        return AuthenticationResponse.builder()
                .token(jwtToken)
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
