package com.example.projectservice.projectmember;

import com.example.projectservice.project.Project;
import com.example.projectservice.project.ProjectCreationRequest;
import com.example.projectservice.project.ProjectUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    public List<ProjectMember> getProjectMembers(){
        return projectMemberRepository.findAll();
    }

    public ProjectMember getProjectMember(Integer id){
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));
        return member;
    }
    public void addUserToProject(ProjectMemberCreationRequest request) {
        ProjectMember member = ProjectMember.builder()
                .memberId(request.memberId())
                .role(request.role())
                .build();

        projectMemberRepository.saveAndFlush(member);
    }

    public void updateProjectMember(Integer id,ProjectMemberUpdateRequest request) {
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));

        member.setRole(request.role());
        projectMemberRepository.save(member);
    }

    public void removeMemberFromProject(Integer id){
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));
        projectMemberRepository.delete(member);
    }
}
