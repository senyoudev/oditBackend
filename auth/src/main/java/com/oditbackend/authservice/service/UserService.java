package com.oditbackend.authservice.service;

import com.oditbackend.authservice.Dto.*;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ProfileResponse getProfile(String authorization){
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalStateException("user with email {email} does not exist"));
        ProfileResponse profile = new ProfileResponse(user.getFirstName(),user.getLastName(),user.getEmail(),user.getRole());

        return profile;
    }

    public AuthenticationResponse updateProfile(String authorization, ProfileUpdateRequest request) {
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalStateException("user with id {id} does not exist"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(jwtToken)
                .message("profile updated ")
                .build();
        return authenticationResponse;
    }

    public AuthenticationResponse updateEmail(String authorization, EmailUpdateRequest request) {
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalStateException("user with email {email} does not exist"));
        if(isValidEmail(request.getEmail())) {
            user.setEmail(request.getEmail());
            userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                    .token(jwtToken)
                    .message("email updated ")
                    .build();

            return authenticationResponse;
        }else{
            throw new IllegalArgumentException("Invalid email format.");
        }
    }
    public AuthenticationResponse updatePassword(String authorization, PasswordUpdateRequest request) {
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalStateException("user with id {id} does not exist"));

        //Todo: password validation
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        AuthenticationResponse authResponse =  AuthenticationResponse.builder()
                .token(jwtToken)
                .message("password updated ")
                .build();
        return authResponse;
    }

    public String deleteAccount(String authorization){
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalStateException("user with id {id} does not exist"));

        userRepository.delete(user);
        return "account deleted";
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[a-zA-Z]{2,})$");
    }
}
