package com.example.roomservice.service;

import com.example.helpers.projects.CustomProjectMemberResponse;
import com.example.roomservice.entity.Room;
import com.example.roomservice.repository.RoomMemberRepository;
import com.example.roomservice.repository.RoomRepository;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMemberRepository roomMemberRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EurekaClient discoveryClient;

    @InjectMocks
    private RoomService roomService;

    private Integer userId = 1;
    private Integer roomId = 123;

    @BeforeEach
    void setUp() {
        InstanceInfo instance = mock(InstanceInfo.class);
        when(instance.getHomePageUrl()).thenReturn("http://localhost:8083");
        when(discoveryClient.getNextServerFromEureka("PROJECT", false)).thenReturn(instance);
        CustomProjectMemberResponse customProjectMemberResponse = CustomProjectMemberResponse.builder()
                .adminEmail("admin@example.com")
                .build();

        when(restTemplate.getForObject(anyString(), eq(CustomProjectMemberResponse.class)))
                .thenReturn(customProjectMemberResponse);
    }
    @Test
    void getProjectRooms() {
        // Given
        Integer projectId = 2;
        List<Room> expectedRooms = Collections.singletonList(Room.builder()
                .id(roomId)
                .projectId(projectId)
                .name("Room 1")
                .description("Description 1")
                .build());
        when(roomRepository.findByProjectId(projectId)).thenReturn(expectedRooms);


        // When
        List<Room> resultRooms = roomService.getProjectRooms(projectId, userId);

        // Then
        assertNotNull(resultRooms);
        assertEquals(expectedRooms, resultRooms);
    }

    @Test
    @Disabled
    void getRoom() {
    }

    @Test
    @Disabled
    void createRoom() {
    }

    @Test
    @Disabled
    void updateRoom() {
    }

    @Test
    @Disabled
    void deleteRoom() {
    }
}