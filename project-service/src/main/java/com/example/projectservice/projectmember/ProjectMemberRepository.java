package com.example.projectservice.projectmember;

import com.example.projectservice.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember,Integer> {
    List<ProjectMember> findProjectMembersByProject(Project project);
    Optional<ProjectMember> findProjectMemberByMemberId(Integer id);
    Optional<ProjectMember> findProjectMemberByUserId(Integer id);
}
