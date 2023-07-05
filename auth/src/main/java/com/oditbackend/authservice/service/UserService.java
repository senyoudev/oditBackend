package com.oditbackend.authservice.service;

import com.example.helpers.exceptions.BadRequestException;
import com.example.helpers.exceptions.NotFoundException;
import com.oditbackend.authservice.Dto.*;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ProfileResponse getProfile(String authorization) {
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user with email " + email + " does not exist"));
        ProfileResponse profile = new ProfileResponse(user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());

        return profile;
    }

    public AuthenticationResponse updateProfile(String authorization, ProfileUpdateRequest request) {
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user with email " + email + " does not exist"));

        try {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            userRepository.save(user);
        } catch (Exception e) {
            throw new BadRequestException("firstName and lastName are required");
        }
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .message("profile updated.")
                .build();
        return authenticationResponse;
    }

    public AuthenticationResponse updateEmail(String authorization, EmailUpdateRequest request) {
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user with email " + email + " does not exist"));

        try {
            if (isValidEmail(request.getEmail())) {
                user.setEmail(request.getEmail());
                userRepository.save(user);
                var jwtToken = jwtService.generateToken(user);
                AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .message("email updated ")
                        .build();

                return authenticationResponse;
            } else {
                throw new BadRequestException("Invalid email format.");
            }
        } catch (Exception e) {
            throw new BadRequestException("firstName and lastName are required");
        }

    }

    public AuthenticationResponse updatePassword(String authorization, PasswordUpdateRequest request) {
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user with email " + email + " does not exist"));

        //Todo: password validation
        try{
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
        }catch (Exception e){
            throw new BadRequestException("password is required");
        }
        var jwtToken = jwtService.generateToken(user);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .message("password updated")
                .build();
        return authenticationResponse;
    }

    public String deleteAccount(String authorization) {
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user with email " + email + " does not exist"));

        userRepository.delete(user);
        return "account deleted";
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[a-zA-Z]{2,})$");
    }
}