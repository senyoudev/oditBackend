package com.example.roomservice.service;

import com.example.helpers.exceptions.BadRequestException;
import com.example.helpers.exceptions.NotFoundException;
import com.example.helpers.exceptions.UnauthorizedException;
import com.example.roomservice.Dto.RoomMemberCreationRequest;
import com.example.roomservice.entity.Room;
import com.example.roomservice.entity.RoomMember;
import com.example.roomservice.repository.RoomMemberRepository;
import com.example.roomservice.repository.RoomRepository;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@AllArgsConstructor
public class RoomMemberService {
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
    private final RestTemplate restTemplate;
    private EurekaClient discoveryClient;

    public List<RoomMember> getRoomMembers(Integer roomId) {
        List<RoomMember> members = roomMemberRepository.findByRoomId(roomId);

        return members;
    }

    public RoomMember getRoomMember(Integer id) {
        RoomMember member = roomMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room member with id " + id + " does not exist"));

        return member;
    }

    public RoomMember addMemberToRoom(Integer userId, RoomMemberCreationRequest request) {

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new NotFoundException("room with id " + request.getRoomId() + " does not exist"));

        InstanceInfo instance = discoveryClient.getNextServerFromEureka("PROJECT", false);
        Boolean isAdmin = restTemplate.getForObject(
                instance.getHomePageUrl() + "/api/v1/projects/checkifadmin?projectId=" + room.getProjectId() + "&userId=" + userId,
                Boolean.class
        );
        Boolean isMember = restTemplate.getForObject(
                instance.getHomePageUrl() + "/api/v1/projectmembers/checkifmember?memberId=" + request.getMemberId(),
                Boolean.class
        );
        if (!isMember) throw new UnauthorizedException("User must be a member in the project");
        if (!isAdmin) throw new UnauthorizedException("You must be admin to add a member to specific room");
        try {
            RoomMember member = RoomMember.builder()
                    .room(room)
                    .memberId(request.getMemberId())
                    .build();

            roomMemberRepository.saveAndFlush(member);
            return member;
        } catch (Exception e) {
            throw new BadRequestException("Your request is not correct");
        }

    }

    public String exitRoom(Integer id,Integer userId) {
        RoomMember roomMember = roomMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room member with id " + id + " does not exist"));

        if(userId != roomMember.getMemberId()) throw new UnauthorizedException("You must join the room first!");
        roomMemberRepository.delete(roomMember);
        return "member exited!";
    }
}
