package com.example.roomservice.service;

import com.example.amqp.RabbitMQMessageProducer;
import com.example.helpers.exceptions.BadRequestException;
import com.example.helpers.exceptions.NotFoundException;
import com.example.helpers.exceptions.UnauthorizedException;
import com.example.helpers.notifications.NotificationRequest;
import com.example.helpers.notifications.NotificationType;
import com.example.helpers.projects.CustomProjectMemberResponse;
import com.example.roomservice.Dto.RoomMemberCreationRequest;
import com.example.roomservice.entity.Room;
import com.example.roomservice.entity.RoomMember;
import com.example.roomservice.repository.RoomMemberRepository;
import com.example.roomservice.repository.RoomRepository;
import com.netflix.discovery.EurekaClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class RoomMemberService {
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
    private final RestTemplate restTemplate;
    private EurekaClient discoveryClient;
    private RabbitMQMessageProducer rabbitMQMessageProducer;

    public Set<RoomMember> getRoomMembers(Integer roomId,Integer userId) {
        Room room = roomRepository.findById(roomId)
                 .orElseThrow(()->new NotFoundException("room with id "+roomId+" not found"));

        try{
            restTemplate.getForObject(
                    "http://project/api/v1/projectmembers/getMemberId?userId=" + userId + "&projectId=" + room.getProjectId(),
                    CustomProjectMemberResponse.class
            );
        }catch (Exception e){
            throw new UnauthorizedException("you must be a project member to get room members");
        }

        Set<RoomMember> members = roomMemberRepository.findByRoomId(roomId);
        return members;
    }

    public RoomMember getRoomMember(Integer id) {
        RoomMember member = roomMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room member with id " + id + " does not exist"));

        Boolean isMember = restTemplate.getForObject(
                "http://project/api/v1/projectmembers/checkifmember?memberId=" + member.getMemberId(),
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

        Boolean isAdmin = restTemplate.getForObject(
                "http://project/api/v1/projects/checkifadmin?projectId=" + room.getProjectId() + "&userId=" + userId,
                Boolean.class
        );
        Boolean isMember = restTemplate.getForObject(
                "http://project/api/v1/projectmembers/checkifmember?memberId=" + request.getMemberId(),
                Boolean.class
        );
        if (Boolean.FALSE.equals(isAdmin))
            throw new UnauthorizedException("You must be admin to add a member to specific room");
        if (Boolean.FALSE.equals(isMember))
            throw new UnauthorizedException("User added must be a member in the project");

        RoomMember member = RoomMember.builder()
                .room(room)
                .memberId(request.getMemberId())
                .build();


        try{
            roomMemberRepository.saveAndFlush(member);
        }catch (Exception e){
            throw new BadRequestException("member already joined the room");
        }

        CustomProjectMemberResponse res = restTemplate.getForObject(
                "http://project/api/v1/projectmembers/getMemberId?userId=" + userId + "&projectId=" + room.getProjectId(),
                CustomProjectMemberResponse.class
        );

        //Todo use other method to get memberEmail (more secured)
        //notify admin that member exit the room
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .from(res.getAdminEmail())
                .to(request.getMemberEmail())
                .type(NotificationType.ACCEPT_NOTIF)
                .build();

        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );

        return member;
    }

    //todo must be tested
    public String exitRoom(Integer id, Integer userId, String username) {
        RoomMember roomMember = roomMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room member with id " + id + " does not exist"));

        try {
            CustomProjectMemberResponse res = restTemplate.getForObject(
                   "http://project/api/v1/projectmembers/getMemberId?userId=" + userId + "&projectId=" + roomMember.getRoom().getProjectId(),
                    CustomProjectMemberResponse.class
            );

            if (!Objects.equals(res.getMemberId(), roomMember.getMemberId()))
                throw new UnauthorizedException("you must be a room member");

            //notify admin that member exit the room
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .from(username)
                    .to(res.getAdminEmail())
                    .type(NotificationType.ACCEPT_NOTIF)
                    .build();


            rabbitMQMessageProducer.publish(
                    notificationRequest,
                    "internal.exchange",
                    "internal.notification.routing-key"
            );
        } catch (Exception e) {
            throw new UnauthorizedException("you must be a project member");
        }

        roomMemberRepository.delete(roomMember);
        return "member exited!";
    }

    //todo must be tested
    public String removeRoomMember(Integer id, Integer userId, String memberEmail) {
        RoomMember roomMember = roomMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("room member with id " + id + " does not exist"));

        Boolean isAdmin = restTemplate.getForObject(
                "http://project/api/v1/projects/checkifadmin?projectId=" + roomMember.getRoom().getProjectId() + "&userId=" + userId,
                Boolean.class
        );
        if (Boolean.FALSE.equals(isAdmin))
            throw new UnauthorizedException("You must be admin to add a member to specific room");

        try {
            CustomProjectMemberResponse res = restTemplate.getForObject(
                    "http://project/api/v1/projectmembers/getMemberId?userId=" + userId + "&projectId=" + roomMember.getRoom().getProjectId(),
                    CustomProjectMemberResponse.class
            );

            //notify admin that member exit the room
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .from(res.getAdminEmail())
                    .to(memberEmail)
                    .type(NotificationType.ACCEPT_NOTIF)
                    .build();


            rabbitMQMessageProducer.publish(
                    notificationRequest,
                    "internal.exchange",
                    "internal.notification.routing-key"
            );
        } catch (Exception e) {
            throw new UnauthorizedException("you must be a project member");
        }

        roomMemberRepository.delete(roomMember);

        return "member removed!";
    }

    public Boolean checkRoomMember(Integer userId, Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("room with id " + roomId + " does not exist"));

        try {
            CustomProjectMemberResponse res = restTemplate.getForObject(
                    "http://project/api/v1/projectmembers/getMemberId?userId=" + userId + "&projectId=" + room.getProjectId(),
                    CustomProjectMemberResponse.class
            );
            roomMemberRepository.findByMemberId(res.getMemberId())
                    .orElseThrow(() -> new NotFoundException("room member with id " + res.getMemberId() + " does not exist"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
