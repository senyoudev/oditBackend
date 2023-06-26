package com.example.projectservice.invite;

import com.example.projectservice.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InviteRepository extends JpaRepository<Invite, Integer> {
    List<Invite> findByInvitedId(Integer invitedId);
}
