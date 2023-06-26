package com.example.projectservice.invite;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;

    public List<Invite> getUserInvitations(Integer invitedId){
        List<Invite> projects = inviteRepository.findByInvitedId(invitedId);
        return projects;
    }
    public Invite getInvitation(Integer id){
        Invite invite = inviteRepository.findById(id)
                .orElseThrow(()->new IllegalStateException("invitation with id {id} does not exist"));
        return invite;
    }
    public Invite sendInvitation(InvitationCreationRequest request) {
        Invite invite = Invite.builder()
                .projectId(request.projectId())
                .invitedId(request.invitedId())
                .build();

        inviteRepository.saveAndFlush(invite);
        return invite;
    }

    public String removeInvitation(Integer id){
        Invite invite = inviteRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));
        inviteRepository.delete(invite);
        return "invitation deleted!";
    }
}
