package com.example.projectservice.invite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InviteRepository extends JpaRepository<Invite, Integer> {

    List<Invite> findByUserEmail(String email);
}
