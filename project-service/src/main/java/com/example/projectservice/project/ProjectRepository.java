package com.example.projectservice.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findByAdminId(Integer adminId);

    Optional<Project> findById(Integer id);

    List<Project> findAll();
}
