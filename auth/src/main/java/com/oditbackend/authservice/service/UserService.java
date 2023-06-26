package com.oditbackend.authservice.service;

import com.oditbackend.authservice.Dto.AuthenticationResponse;
import com.oditbackend.authservice.Dto.EmailUpdateRequest;
import com.oditbackend.authservice.Dto.PasswordUpdateRequest;
import com.oditbackend.authservice.Dto.ProfileUpdateRequest;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
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

    public ResponseEntity<User> getProfile(Integer id){
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) return (ResponseEntity<User>) ResponseEntity.notFound();
        return ResponseEntity.ok(user.get());
    }

    public ResponseEntity<AuthenticationResponse> updateProfile(Integer id, ProfileUpdateRequest request) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) return (ResponseEntity<AuthenticationResponse>) ResponseEntity.notFound();
        user.get().setFirstName(request.getFirstName());
        user.get().setLastName(request.getLastName());
        userRepository.save(user.get());
        var jwtToken = jwtService.generateToken(user.get());
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(jwtToken)
                .message("profile updated ")
                .build();
        return ResponseEntity.ok(authenticationResponse);
    }

    public ResponseEntity<AuthenticationResponse> updateEmail(Integer id, EmailUpdateRequest request) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) return (ResponseEntity<AuthenticationResponse>) ResponseEntity.notFound();
        user.get().setEmail(request.getEmail());
        userRepository.save(user.get());
        var jwtToken = jwtService.generateToken(user.get());
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(jwtToken)
                .message("email updated ")
                .build();

        return ResponseEntity.ok(authenticationResponse);
    }
    public ResponseEntity<AuthenticationResponse> updatePassword(Integer id, PasswordUpdateRequest request) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) return  (ResponseEntity<AuthenticationResponse>) ResponseEntity.notFound();

        user.get().setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user.get());
        var jwtToken = jwtService.generateToken(user.get());

        AuthenticationResponse authResponse =  AuthenticationResponse.builder()
                .token(jwtToken)
                .message("password updated ")
                .build();
        return ResponseEntity.ok(authResponse);
    }

    public ResponseEntity<String> deleteAccount(Integer id){
        Optional<User> user = userRepository
                .findById(id);
        if (!user.isPresent()) return (ResponseEntity<String>) ResponseEntity.notFound();

        userRepository.delete(user.get());
        return  ResponseEntity.ok("account deleted");
    }
}
