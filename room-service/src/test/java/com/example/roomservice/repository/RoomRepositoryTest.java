package com.example.roomservice.repository;

import com.example.roomservice.entity.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomRepositoryTest {

    @Mock
    private RoomRepository underTest;

    @Test
    void findByProjectId() {
        // Given
        int projectId = 123;
        List<Room> rooms = new ArrayList<>();
        rooms.add(Room.builder().id(1).name("Room 1").projectId(projectId).build());
        rooms.add(Room.builder().id(2).name("Room 2").projectId(projectId).build());

        // When
        when(underTest.findByProjectId(projectId)).thenReturn(rooms);
        List<Room> resultRooms = underTest.findByProjectId(projectId);

        // Then
        assertEquals(2, resultRooms.size());
        assertEquals(1, resultRooms.get(0).getId());

    }
}