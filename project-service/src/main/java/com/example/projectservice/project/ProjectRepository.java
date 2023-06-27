package com.example.projectservice.project;

import com.example.projectservice.projectmember.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findByUserId(Integer userId);

    Optional<Project> findById(Integer id);

    List<Project> findAll();
}
