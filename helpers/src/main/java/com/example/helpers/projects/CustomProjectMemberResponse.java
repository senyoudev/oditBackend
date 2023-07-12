package com.example.helpers.projects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomProjectMemberResponse {
    private Integer memberId;
    private String adminEmail;
}
