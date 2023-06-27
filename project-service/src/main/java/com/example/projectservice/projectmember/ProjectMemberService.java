package com.example.projectservice.projectmember;

import com.example.projectservice.project.Project;
import com.example.projectservice.project.ProjectCreationRequest;
import com.example.projectservice.project.ProjectRepository;
import com.example.projectservice.project.ProjectUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    public List<ProjectMember> getProjectMembers(Integer projectId){
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(()->new IllegalStateException("project with id "+projectId+" does not exist"));
        List <ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
        return members;
    }
    public ProjectMember getProjectMember(Integer id){
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("member with id {id} does not exist"));

        return member;
    }
    public ProjectMember addUserToProject(ProjectMemberCreationRequest request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));
        ProjectMember member = ProjectMember.builder()
                .memberId(request.memberId())
                .role(MemberRole.MEMBER)
                .build();

        projectMemberRepository.saveAndFlush(member);
        return member;
    }

    public ProjectMember updateProjectMember(Integer id,ProjectMemberUpdateRequest request) {
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("member with id {id} does not exist"));

        member.setRole(request.role());
        projectMemberRepository.save(member);

        return member;
    }

    public String removeMemberFromProject(Integer id){
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("member with id {id} does not exist"));
        projectMemberRepository.delete(member);
        return "Member Removed from project";
    }
}
