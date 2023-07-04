package com.example.projectservice.invite;

import com.example.amqp.RabbitMQMessageProducer;
import com.example.helpers.notifications.NotificationRequest;
import com.example.helpers.notifications.NotificationType;
import com.example.projectservice.project.Project;
import com.example.projectservice.project.ProjectNotFoundException;
import com.example.projectservice.project.ProjectRepository;
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


    public Invite getInvitationById(Integer id) {
        Invite invite = inviteRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("invitation with id {id} does not exist"));
        return invite;
    }

    public List<Invite> getInvitationsByUserEmail(String userEmail) {
        return inviteRepository.findByUserEmail(userEmail);
    }

    public Invite sendInvitation(Integer userId, InvitationCreationRequest request, String username) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        if (userId != project.getAdminId()) {
            throw new UnauthorizedException("Only project owners can send invitations.");
        }
        if (request.getUserEmail().equals(username)) {
            throw new UnauthorizedException("You can't send invitation to your self.");
        }
        Invite invite = Invite.builder()
                .project(project)
                .userEmail(request.getUserEmail())
                .isAccepted(false)
                .build();

        inviteRepository.saveAndFlush(invite);

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

    public void acceptInvitation(Integer invitationId, String username) {
        Invite invitation = getInvitationById(invitationId);
        if (!invitation.getUserEmail().equals(username)) {
            throw new UnauthorizedException("You Can Accept Only Your Invitations");
        }
        invitation.setIsAccepted(true);
        inviteRepository.save(invitation);

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
    }

    public String declineInvitation(Integer invitationId, String username) {
        Invite invitation = getInvitationById(invitationId);
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
