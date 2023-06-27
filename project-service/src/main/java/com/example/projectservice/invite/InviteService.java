package com.example.projectservice.invite;

import com.example.projectservice.project.Project;
import com.example.projectservice.project.ProjectNotFoundException;
import com.example.projectservice.project.ProjectRepository;
import com.example.projectservice.project.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;


    public Invite getInvitationById(Integer id){
        Invite invite = inviteRepository.findById(id)
                .orElseThrow(()->new IllegalStateException("invitation with id {id} does not exist"));
        return invite;
    }

    public List<Invite> getInvitationsByUserEmail(String userEmail) {
        return inviteRepository.findByUserEmail(userEmail);
    }

    public Invite sendInvitation(Integer userId,InvitationCreationRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        if(userId != project.getUserId()) {
            throw new UnauthorizedException("Only project owners can send invitations.");
        }
            Invite invite = Invite.builder()
                    .project(project)
                    .adminId(userId)
                    .userEmail(request.getUserEmail())
                    .isAccepted(false)
                    .build();

            inviteRepository.saveAndFlush(invite);
            //here we should send a notification to the invited user
            return invite;

    }

    public void acceptInvitation(Integer invitationId) {
        Invite invitation = getInvitationById(invitationId);
        invitation.setIsAccepted(true);
        inviteRepository.save(invitation);
        //here we send a notif to the admin
    }

    public String declineInvitation(Integer invitationId) {
        Invite invitation = getInvitationById(invitationId);
        inviteRepository.delete(invitation);
        //here we send a notif to the admin
        return "Deleted Successfully";
    }




}
