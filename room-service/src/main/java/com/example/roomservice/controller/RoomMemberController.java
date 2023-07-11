package com.example.roomservice.controller;

import com.example.roomservice.Dto.RoomMemberCreationRequest;
import com.example.roomservice.entity.RoomMember;
import com.example.roomservice.service.RoomMemberService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/v1/room-members")
@AllArgsConstructor
public class RoomMemberController {
    private final RoomMemberService roomMemberService;

    @GetMapping
    public Set<RoomMember> getRoomMembers(@RequestParam Integer roomId) {
        return roomMemberService.getRoomMembers(roomId);
    }

    @GetMapping(value = "{id}")
    public RoomMember getRoomMember(@PathVariable("id") Integer id) {
        return roomMemberService.getRoomMember(id);
    }

    @PostMapping
    public RoomMember addMemberToRoom(@RequestParam Integer userId,@RequestBody RoomMemberCreationRequest request){
        return roomMemberService.addMemberToRoom(userId,request);
    }

    @DeleteMapping(value = "{id}")
    public String exitRoom(@PathVariable("id") Integer id,@RequestParam Integer userId){
        return roomMemberService.exitRoom(id,userId);
    }

    @DeleteMapping(value = "{id}/exit")
    public String removeRoomMember(@PathVariable("id") Integer id,@RequestParam Integer userId){
        return roomMemberService.removeRoomMember(id,userId);
    }

    @GetMapping("/checkRoomMember")
    public Boolean checkRoomMember(@RequestParam Integer userId) {
        return roomMemberService.checkRoomMember(userId);
    }
}
