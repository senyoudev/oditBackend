package com.oditbackend.authservice.Dto;

import com.oditbackend.authservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
