package com.example.projectservice.invite;

import com.example.amqp.RabbitMQMessageProducer;
import com.example.helpers.exceptions.BadRequestException;
import com.example.helpers.exceptions.NotFoundException;
import com.example.helpers.exceptions.UnauthorizedException;
import com.example.helpers.notifications.NotificationRequest;
import com.example.helpers.notifications.NotificationType;
import com.example.projectservice.project.Project;
import com.example.projectservice.project.ProjectRepository;
import com.example.projectservice.projectmember.ProjectMemberCreationRequest;
import com.example.projectservice.projectmember.ProjectMemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final ProjectRepository projectRepository;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    private final ProjectMemberService projectMemberService;


    public Invite getInvitationById(Integer id,Integer userId,String username) {
        Invite invite = inviteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("invitation with id "+id+" does not exist"));

        if(userId != invite.getProject().getAdminId() && username != invite.getUserEmail()){
            throw new UnauthorizedException("user must be sender or receiver");
        }
        return invite;
    }

    public List<Invite> getInvitationsByUserEmail(String userEmail) {
        return inviteRepository.findByUserEmail(userEmail);
    }

    public Invite sendInvitation(Integer userId, InvitationCreationRequest request, String username) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found"));

        if (userId != project.getAdminId()) {
            throw new UnauthorizedException("Only project owners can send invitations.");
        }
        if (request.getUserEmail().equals(username)) {
            throw new UnauthorizedException("You can't send invitation to your self.");
        }
        Invite invite = null;
        try {
            invite = Invite.builder()
                    .project(project)
                    .userEmail(request.getUserEmail())
                    .build();

            inviteRepository.saveAndFlush(invite);
        } catch (Exception e) {
            throw new BadRequestException("Email is required");
        }

        //send a notification to the invited user
        NotificationRequest notificationRequest = new NotificationRequest(
                username,
                invite.getUserEmail(),
                NotificationType.INVITATION
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
        return invite;
    }

    public void acceptInvitation(Integer invitationId, Integer userId, String username) {
        Invite invitation = getInvitationById(invitationId,userId,username);
        if (!invitation.getUserEmail().equals(username)) {
            throw new UnauthorizedException("You Can Accept Only Your Invitations");
        }

        //Add user to project
        ProjectMemberCreationRequest request = new ProjectMemberCreationRequest(invitation.getProject().getId(), userId);
        projectMemberService.addUserToProject(request);

        //send an accept notification to the admin
        NotificationRequest notificationRequest = new NotificationRequest(
                invitation.getUserEmail(),
                invitation.getProject().getAdminEmail(),
                NotificationType.ACCEPT_NOTIF
        );

        //Todo remove accepted from invitation model
        //Delete Invitation
        inviteRepository.delete(invitation);

        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
    }

    public String declineInvitation(Integer invitationId, Integer userId,String username) {
        Invite invitation = getInvitationById(invitationId,userId,username);
        if (!invitation.getUserEmail().equals(username)) {
            throw new UnauthorizedException("You Can Remove Only Your Invitations");
        }
        inviteRepository.delete(invitation);

        //send an accept notification to the admin
        NotificationRequest notificationRequest = new NotificationRequest(
                invitation.getUserEmail(),
                invitation.getProject().getAdminEmail(),
                NotificationType.ACCEPT_NOTIF
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );

        return "Deleted Successfully";

    }


}
