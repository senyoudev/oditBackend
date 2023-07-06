package com.example.roomservice.service;

import com.example.helpers.exceptions.BadRequestException;
import com.example.helpers.exceptions.NotFoundException;
import com.example.helpers.exceptions.UnauthorizedException;
import com.example.roomservice.Dto.RoomCreationRequest;
import com.example.roomservice.Dto.RoomUpdateRequest;
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
import java.util.Set;

@Service
@AllArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RestTemplate restTemplate;
    private EurekaClient discoveryClient;

    public List<Room> getProjectRooms(Integer projectId,Integer userId) {
        List<Room> rooms = roomRepository.findByProjectId(projectId);
        //Todo only project members can view project rooms
        for (Room room : rooms) {
            Set<RoomMember> members = roomMemberRepository.findRoomMembersByRoom(room);
            room.setMembers(members);
        }

        return rooms;
    }

    public Room getRoom(Integer userId,Integer id) {
        //Todo only project members can view project room
        Room room = roomRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room with id " + id + " does not exist"));
        Set<RoomMember> members = roomMemberRepository.findRoomMembersByRoom(room);
        room.setMembers(members);
        return room;
    }

    public Room createRoom(Integer userId, RoomCreationRequest request) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka("PROJECT", false);
        Boolean isAdmin = restTemplate.getForObject(
                instance.getHomePageUrl() + "/api/v1/projects/checkifadmin?projectId=" + request.getProjectId() + "&userId=" + userId,
                Boolean.class
        );
        if (!isAdmin) throw new UnauthorizedException("Your must be admin to create room");
        try {
            Room room = Room.builder()
                    .projectId(request.getProjectId())
                    .name(request.getName())
                    .description(request.getDescription())
                    .build();

            roomRepository.saveAndFlush(room);
            return room;
        } catch (Exception e) {
            throw new BadRequestException("Your request is not correct");
        }
    }

    public Room updateRoom(Integer id, Integer userId, RoomUpdateRequest request) {
        Room room = roomRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room with id " + id + " does not exist"));

        InstanceInfo instance = discoveryClient.getNextServerFromEureka("PROJECT", false);
        Boolean isAdmin = restTemplate.getForObject(
                instance.getHomePageUrl() + "/api/v1/projects/checkifadmin?projectId=" + room.getProjectId() + "&userId=" + userId,
                Boolean.class
        );
        if (!isAdmin) throw new UnauthorizedException("Your must be admin to update room");
        try {
            room.setName(request.getName());
            room.setDescription(request.getDescription());

            return room;
        } catch (Exception e) {
            throw new BadRequestException("Your request is not correct");
        }
    }

    public String deleteRoom(Integer id, Integer userId) {
        Room room = roomRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room with id " + id + " does not exist"));
        InstanceInfo instance = discoveryClient.getNextServerFromEureka("PROJECT", false);
        Boolean isAdmin = restTemplate.getForObject(
                instance.getHomePageUrl() + "/api/v1/projects/checkifadmin?projectId=" + room.getProjectId() + "&userId=" + userId,
                Boolean.class
        );
        if (!isAdmin) throw new UnauthorizedException("Your must be admin to delete room");
        roomRepository.delete(room);
        return "room deleted!";
    }
}
