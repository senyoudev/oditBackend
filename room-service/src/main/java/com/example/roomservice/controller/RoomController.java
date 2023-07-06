package com.example.roomservice.controller;

import com.example.roomservice.Dto.RoomCreationRequest;
import com.example.roomservice.Dto.RoomUpdateRequest;
import com.example.roomservice.entity.Room;
import com.example.roomservice.service.RoomService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/rooms")
@AllArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public List<Room> getProjectRooms(@RequestParam Integer projectId,@RequestParam Integer userId) {
        return roomService.getProjectRooms(projectId,userId);
    }

    @GetMapping(value = "{id}")
    public Room getRoom(@PathVariable("id") Integer id,@RequestParam Integer userId) {
        return roomService.getRoom(id,userId);
    }

    @PostMapping
    public Room createRoom(@RequestParam Integer userId,@RequestBody RoomCreationRequest request){
        return roomService.createRoom(userId,request);
    }

    @PutMapping(value = "{id}")
    public Room updateRoom(@PathVariable("id") Integer id,@RequestParam Integer userId,@RequestBody RoomUpdateRequest request){
        return roomService.updateRoom(id,userId,request);
    }

    @DeleteMapping(value = "{id}")
    public String deleteRoom(@PathVariable("id") Integer id,@RequestParam Integer userId){
        return roomService.deleteRoom(id,userId);
    }

}
