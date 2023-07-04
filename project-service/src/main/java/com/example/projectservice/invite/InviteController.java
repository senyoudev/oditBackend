package com.example.projectservice.invite;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/invitations")
@AllArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    @GetMapping
    public List<Invite> getUserInvitations(@RequestParam String username) {
        return inviteService.getInvitationsByUserEmail(username);
    }

    @GetMapping(value = "{id}")
    public Invite getInvitation(@PathVariable("id") Integer id) {
        return inviteService.getInvitationById(id);
    }

    @PostMapping
    public void sendInvitation(@RequestParam Integer userId,@RequestBody InvitationCreationRequest request,@RequestParam String username){
        inviteService.sendInvitation(userId,request,username);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable("id") Integer invitationId,@RequestParam String username) {
        inviteService.acceptInvitation(invitationId,username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "{id}")
    public String removeInvitation(@PathVariable("id") Integer id,@RequestParam String username){
        return inviteService.declineInvitation(id,username);
    }
}
