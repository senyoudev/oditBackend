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
        InstanceInfo instance = discoveryClient.getNextServerFromEureka("PROJECT", false);
        List<RoomMember> members = roomMemberRepository.findByRoomId(roomId);
        for (RoomMember member : members) {
            Boolean isMember = restTemplate.getForObject(
                    instance.getHomePageUrl() + "/api/v1/projectmembers/checkifmember?memberId=" + member.getMemberId(),
                    Boolean.class
            );

            if (isMember == null || !isMember) {
                throw new UnauthorizedException("Member with ID " + member.getMemberId() + " is not authorized.");
            }
        }

        return members;
    }

    public RoomMember getRoomMember(Integer id) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka("PROJECT", false);
        RoomMember member = roomMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room member with id " + id + " does not exist"));

        Boolean isMember = restTemplate.getForObject(
                instance.getHomePageUrl() + "/api/v1/projectmembers/checkifmember?memberId=" + member.getMemberId(),
                Boolean.class
        );

        if (isMember == null || !isMember) {
            throw new UnauthorizedException("Member with ID " + member.getMemberId() + " is not authorized.");
        }

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
        if (!isAdmin) throw new UnauthorizedException("You must be admin to add a member to specific room");
        if (!isMember) throw new UnauthorizedException("User added must be a member in the project");
        try {
            RoomMember member = RoomMember.builder()
                    .room(room)
                    .memberId(request.getMemberId())
                    .build();

            roomMemberRepository.saveAndFlush(member);

            //Todo: send email to member
            return member;
        } catch (Exception e) {
            throw new BadRequestException("Your request is not correct");
        }

    }

    public String exitRoom(Integer id,Integer userId) {
        RoomMember roomMember = roomMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room member with id " + id + " does not exist"));

        //Todo request sended must be the person in the room
        roomMemberRepository.delete(roomMember);
        return "member exited!";
    }

    public String removeRoomMember(Integer id,Integer userId) {
        RoomMember roomMember = roomMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room member with id " + id + " does not exist"));

        InstanceInfo instance = discoveryClient.getNextServerFromEureka("PROJECT", false);
        Boolean isAdmin = restTemplate.getForObject(
                instance.getHomePageUrl() + "/api/v1/projects/checkifadmin?projectId=" + roomMember.getRoom().getProjectId() + "&userId=" + userId,
                Boolean.class
        );
        if (!isAdmin) throw new UnauthorizedException("You must be admin to add a member to specific room");

        roomMemberRepository.delete(roomMember);

        //Todo: send email to member
        return "member exited!";
    }
}
