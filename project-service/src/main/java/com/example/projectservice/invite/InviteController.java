package com.example.projectservice.invite;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/invitations")
@AllArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    @GetMapping
    public List<Invite> getUserInvitations(@RequestParam Integer userId) {
        return inviteService.getUserInvitations(userId);
    }

    @GetMapping(value = "{id}")
    public Invite getInvitation(@PathVariable("id") Integer id) {
        return inviteService.getInvitation(id);
    }

    @PostMapping
    public Invite sendInvitation(@RequestBody InvitationCreationRequest request){
        return inviteService.sendInvitation(request);
    }

    @DeleteMapping(value = "{id}")
    public String removeInvitation(@PathVariable("id") Integer id){
        return inviteService.removeInvitation(id);
    }
}
