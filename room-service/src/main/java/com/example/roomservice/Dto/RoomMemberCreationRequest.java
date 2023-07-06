package com.example.roomservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomMemberCreationRequest {
    private Integer roomId;
    private Integer memberId;
}

