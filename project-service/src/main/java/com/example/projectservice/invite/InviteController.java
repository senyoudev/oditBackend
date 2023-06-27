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
    public List<Invite> getUserInvitations(@RequestBody String email) {
        return inviteService.getInvitationsByUserEmail(email);
    }

    @GetMapping(value = "{id}")
    public Invite getInvitation(@PathVariable("id") Integer id) {
        return inviteService.getInvitationById(id);
    }

    @PostMapping
    public Invite sendInvitation(@RequestParam Integer userId,@RequestBody InvitationCreationRequest request){
        return inviteService.sendInvitation(userId,request);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable("id") Integer invitationId) {
        inviteService.acceptInvitation(invitationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "{id}")
    public String removeInvitation(@PathVariable("id") Integer id){
        return inviteService.declineInvitation(id);
    }
}
