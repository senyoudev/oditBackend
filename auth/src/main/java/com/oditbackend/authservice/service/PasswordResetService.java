package com.oditbackend.authservice.service;


import com.example.amqp.RabbitMQMessageProducer;
import com.example.helpers.notifications.NotificationRequest;
import com.example.helpers.notifications.NotificationType;
import com.oditbackend.authservice.entity.PasswordResetToken;
import com.oditbackend.authservice.entity.User;
import com.oditbackend.authservice.exceptions.ExpiredTokenException;
import com.oditbackend.authservice.exceptions.InvalidTokenException;
import com.oditbackend.authservice.repository.PasswordResetTokenRepository;
import com.oditbackend.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private static final int EXPIRATION_HOURS = 24;

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;




    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        PasswordResetToken resetToken = generateToken(user);

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .from("odit.contact@gmail.com")
                .to(user.getEmail())
                .recipient(user.getFirstName())
                .resetToken(resetToken.getToken())
                .type(NotificationType.PASSWORD_RESET_NOTIF)
                .build();
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );



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
        String token = uuid.toString().substring(0, 6);
        return token;
    }



}
