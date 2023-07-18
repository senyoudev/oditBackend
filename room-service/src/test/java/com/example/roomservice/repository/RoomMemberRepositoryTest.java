package com.example.roomservice.repository;

import com.example.roomservice.Dto.RoomMemberCreationRequest;
import com.example.roomservice.entity.Room;
import com.example.roomservice.entity.RoomMember;
import com.example.roomservice.service.RoomMemberService;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomMemberRepositoryTest {

    @Mock
    private RoomMemberRepository underTest;
    @InjectMocks
    private RoomMemberService roomMemberService;

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private EurekaClient discoveryClient;

    Integer userId = 1;
    Integer roomId = 123;
    Integer firstmemberId = 1;
    Integer secondmemberId = 2;

    @BeforeEach
    void setUp() {
        //create a room
        Room room = Room.builder().id(roomId).name("Room 1").projectId(1).build();
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Mock check if a user is a member using the RestTemplate
        InstanceInfo instance = mock(InstanceInfo.class);
        when(instance.getHomePageUrl()).thenReturn("http://localhost:8083");
        when(discoveryClient.getNextServerFromEureka("PROJECT", false)).thenReturn(instance);
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);

        //add 2 members to a room
        RoomMemberCreationRequest firstroomMemberCreationRequest = RoomMemberCreationRequest
                .builder()
                .memberId(firstmemberId)
                .roomId(roomId)
                .build();
        RoomMemberCreationRequest secondroomMemberCreationRequest = RoomMemberCreationRequest
                .builder()
                .memberId(secondmemberId)
                .roomId(roomId)
                .build();

        when(underTest.saveAndFlush(ArgumentMatchers.any(RoomMember.class))).thenAnswer(invocation -> invocation.getArgument(0));


        roomMemberService.addMemberToRoom(firstmemberId,firstroomMemberCreationRequest);
        roomMemberService.addMemberToRoom(secondmemberId,secondroomMemberCreationRequest);


    }


    @Test
    void ItShouldfindMembersByRoomId() {
        // Given
        Set<RoomMember> expectedMembers = new HashSet<>();
        RoomMember member1 = RoomMember.builder().id(1).memberId(firstmemberId).build();
        RoomMember member2 = RoomMember.builder().id(2).memberId(secondmemberId).build();
        expectedMembers.add(member1);
        expectedMembers.add(member2);

        // When
        when(underTest.findByRoomId(roomId)).thenReturn(expectedMembers);
        Set<RoomMember> resultMembers = roomMemberService.getRoomMembers(roomId);

        // Then
        assertNotNull(resultMembers);
        assertEquals(expectedMembers, resultMembers);
    }

    @Test
    @Disabled
    void findRoomMembersByRoom() {
        // Given
        Room room = Room.builder().id(roomId).projectId(2).name("Room 2").build();
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Set<RoomMember> expectedMembers = new HashSet<>();
        RoomMember member1 = RoomMember.builder().id(3).memberId(firstmemberId).room(room).build();
        expectedMembers.add(member1);
        when(underTest.findRoomMembersByRoom(room)).thenReturn(expectedMembers);

        // When
        Set<RoomMember> resultMembers = roomMemberService.getRoomMembers(roomId);

        // Then
        assertNotNull(resultMembers);
        assertEquals(expectedMembers, resultMembers);
        }



    @Test
    @Disabled
    void findByMemberId() {
    }
}