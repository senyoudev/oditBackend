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
    public Set<RoomMember> getRoomMembers(@RequestParam Integer roomId,@RequestParam Integer userId) {
        return roomMemberService.getRoomMembers(roomId,userId);
    }

    @GetMapping(value = "{id}")
    public RoomMember getRoomMember(@PathVariable("id") Integer id) {
        return roomMemberService.getRoomMember(id);
    }

    @GetMapping(value = "/memberId")
    public Set<RoomMember> getRoomsMemberId(@RequestParam Integer projectId, @RequestParam Integer userId) {
        return roomMemberService.getRoomsMemberId(projectId,userId);
    }

    @PostMapping
    public RoomMember addMemberToRoom(@RequestParam Integer userId,@RequestBody RoomMemberCreationRequest request){
        return roomMemberService.addMemberToRoom(userId,request);
    }

    @DeleteMapping(value = "{id}")
    public String exitRoom(@PathVariable("id") Integer id,@RequestParam Integer userId,@RequestParam String username){
        return roomMemberService.exitRoom(id,userId,username);
    }

    @DeleteMapping(value = "{id}/exit")
    public String removeRoomMember(@PathVariable("id") Integer id,@RequestParam Integer userId,@RequestParam String memberEmail){
        return roomMemberService.removeRoomMember(id,userId,memberEmail);
    }

    @GetMapping("/checkRoomMember")
    public Boolean checkRoomMember(@RequestParam Integer userId,@RequestParam Integer roomId) {
        return roomMemberService.checkRoomMember(userId,roomId);
    }
}
