package com.oditbackend.authservice.service;


import com.oditbackend.authservice.entity.PasswordResetToken;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.exceptions.ExpiredTokenException;
import com.oditbackend.authservice.exceptions.InvalidTokenException;
import com.oditbackend.authservice.repository.PasswordResetTokenRepository;
import com.oditbackend.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PasswordResetService {
    private static final int EXPIRATION_HOURS = 24;

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;




    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        PasswordResetToken resetToken = generateToken(user);
        log.info(resetToken.getToken());



        // Send the password reset link via email (call notification service)
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = validateToken(token);

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the used password reset token
        tokenRepository.delete(resetToken);
    }

    public PasswordResetToken generateToken(User user) {
        String token = generateUniqueToken();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS));

        return tokenRepository.save(resetToken);
    }

    public PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException("Reset token has expired.");
        }

        return resetToken;
    }

    private String generateUniqueToken() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }



}
