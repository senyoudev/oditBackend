package com.example.projectservice.projectmember;

import com.example.projectservice.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember,Integer> {
    List<ProjectMember> findProjectMembersByProject(Project project);
    Optional<ProjectMember> findProjectMemberByUserIdAndProject(Integer id,Project project);
    Optional<ProjectMember> findByRole(MemberRole role);
}
