package com.example.projectservice.invite;

import com.example.amqp.RabbitMQMessageProducer;
import com.example.helpers.notifications.NotificationRequest;
import com.example.projectservice.project.Project;
import com.example.projectservice.project.ProjectNotFoundException;
import com.example.projectservice.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InviteService {

    private final InviteRepository inviteRepository;
    private final ProjectRepository projectRepository;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    public InviteService(InviteRepository inviteRepository, ProjectRepository projectRepository, RabbitMQMessageProducer rabbitMQMessageProducer) {
        this.inviteRepository = inviteRepository;
        this.projectRepository = projectRepository;
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
    }


    public Invite getInvitationById(Integer id){
        Invite invite = inviteRepository.findById(id)
                .orElseThrow(()->new IllegalStateException("invitation with id {id} does not exist"));
        return invite;
    }

    public List<Invite> getInvitationsByUserEmail(String userEmail) {
        return inviteRepository.findByUserEmail(userEmail);
    }

    public Invite sendInvitation(Integer userId,InvitationCreationRequest request,String username) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        if(userId != project.getUserId() ) {
            throw new UnauthorizedException("Only project owners can send invitations.");
        }
        if(!request.getUserEmail().equals(username)) {
            throw new UnauthorizedException("You can't send invitation to your self.");
        }
            Invite invite = Invite.builder()
                    .project(project)
                    .adminId(userId)
                    .userEmail(request.getUserEmail())
                    .isAccepted(false)
                    .build();

            inviteRepository.saveAndFlush(invite);

            //send a notification to the invited user
            NotificationRequest notificationRequest = new NotificationRequest(
                    invite.getUserEmail(),
                    username
            );
            rabbitMQMessageProducer.publish(
                    notificationRequest,
                    exchange,
                    routingKey
            );
            return invite;

    }

    public void acceptInvitation(Integer invitationId,String username) {
        Invite invitation = getInvitationById(invitationId);
        if(!invitation.getUserEmail().equals(username)) {
            throw new UnauthorizedException("You Can Accept Only Your Invitations");
       }
        invitation.setIsAccepted(true);
        inviteRepository.save(invitation);

        //send an accept notification to the admin
        NotificationRequest notificationRequest = new NotificationRequest(
                invitation.getUserEmail(),
                username
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                exchange,
                routingKey
        );
    }

    public String declineInvitation(Integer invitationId,String username) {
        Invite invitation = getInvitationById(invitationId);
        if(!invitation.getUserEmail().equals(username)) {
            throw new UnauthorizedException("You Can Accept Only Your Invitations");
        }
        inviteRepository.delete(invitation);

        //send an accept notification to the admin
        NotificationRequest notificationRequest = new NotificationRequest(
                invitation.getUserEmail(),
                username
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                exchange,
                routingKey
        );

        return "Deleted Successfully";
    }




}
