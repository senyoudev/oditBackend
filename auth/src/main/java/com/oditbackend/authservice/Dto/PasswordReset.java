package com.oditbackend.authservice.Dto;

import lombok.Getter;

@Getter
public class PasswordReset {
    private String token;
    private String newPassword;


}
