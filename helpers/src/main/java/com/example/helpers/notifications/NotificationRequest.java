package com.example.helpers.notifications;


import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    String from;
    String to;
    @Nullable
    String InviteLink;
    @Nullable
    String recipient;

    @Nullable
    String resetToken;

    NotificationType type;

}
