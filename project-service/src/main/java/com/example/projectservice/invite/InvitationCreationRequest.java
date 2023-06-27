package com.example.projectservice.invite;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationCreationRequest{

    Integer projectId;
    String userEmail;

}

