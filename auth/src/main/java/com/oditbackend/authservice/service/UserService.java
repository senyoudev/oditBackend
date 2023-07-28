package com.oditbackend.authservice.service;

import com.example.helpers.exceptions.BadRequestException;
import com.example.helpers.exceptions.NotFoundException;
import com.oditbackend.authservice.Dto.*;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.repository.UserRepository;
import jakarta.ws.rs.InternalServerErrorException;
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
        ProfileResponse profile = new ProfileResponse(user.getId(),user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());

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

        validatePassword(request.getPassword());
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

    public AuthenticationResponse updatePicture(String authorization, String pictureUrl) {
        String token = authorization.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " does not exist"));

        try {
            user.setPicture(pictureUrl);
            userRepository.save(user);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update picture");
        }

        var jwtToken = jwtService.generateToken(user);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .message("Picture updated")
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

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long.");
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            throw new BadRequestException("Password must contain at least one uppercase letter.");
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            throw new BadRequestException("Password must contain at least one lowercase letter.");
        }

        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            throw new BadRequestException("Password must contain at least one digit.");
        }

        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()].*")) {
            throw new BadRequestException("Password must contain at least one special character (!@#$%^&*()).");
        }
    }
}