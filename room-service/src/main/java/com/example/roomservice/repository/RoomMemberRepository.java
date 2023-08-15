package com.example.roomservice.repository;

import com.example.roomservice.entity.Room;
import com.example.roomservice.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Integer> {
    Set<RoomMember> findByRoomId(int roomId);
    Set<RoomMember> findRoomMembersByRoom(Room room);

    Optional<RoomMember> findByMemberId(Integer memberId);
    Set<RoomMember> findAllByMemberId(Integer memberId);
}
