package com.example.projectservice.projectmember;

import com.example.helpers.exceptions.BadRequestException;
import com.example.helpers.exceptions.NotFoundException;
import com.example.helpers.exceptions.UnauthorizedException;
import com.example.helpers.projects.CustomProjectMemberResponse;
import com.example.projectservice.project.Project;
import com.example.projectservice.project.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;

    public List<ProjectMember> getProjectMembers(Integer projectId, Integer userId) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException("project with id " + projectId + " does not exist"));

        if (!project.getIsPublic()) {
            projectMemberRepository.findProjectMemberByUserIdAndProject(userId,project)
                    .orElseThrow(() -> new UnauthorizedException("You must be a member in this project"));
        }

        return projectMemberRepository.findProjectMembersByProject(project);
    }

    public ProjectMember getProjectMember(Integer id, Integer userId) {
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("member with id "+id+" does not exist"));
        if (!Objects.equals(userId, member.getUserId()))
            throw new UnauthorizedException("You must be a member in this project");
        return member;
    }

    public void addUserToProject(ProjectMemberCreationRequest request,MemberRole role) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new NotFoundException("project with id "+request.projectId()+" does not exist"));
        try {
            ProjectMember member = ProjectMember.builder()
                    .project(project)
                    .userId(request.userId())
                    .role(role)
                    .build();

            projectMemberRepository.saveAndFlush(member);
        } catch (Exception e) {
            throw new BadRequestException("Your request is not correct");
        }
    }

    public ProjectMember updateProjectMember(Integer id, Integer userId, ProjectMemberUpdateRequest request) {
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("member with id " + id + "does not exist"));
        if (!Objects.equals(userId, member.getProject().getAdminId()))
            throw new UnauthorizedException("You are not permited to do this operation");
        try {
            member.setRole(request.role());
            projectMemberRepository.save(member);

            return member;
        } catch (Exception e) {
            throw new BadRequestException("Role is required!");
        }
    }

    public String removeMemberFromProject(Integer id, Integer userId) {
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("member with id " + id + " does not exist"));
        if (!Objects.equals(userId, member.getProject().getAdminId()))
            throw new UnauthorizedException("You are not permited to do this operation");
        projectMemberRepository.delete(member);
        return "Member Removed from project";
    }

    public Boolean checkIfMember(Integer memberId) {
        try {
            projectMemberRepository
                    .findById(memberId).orElseThrow(() -> new NotFoundException("member with id " + memberId + " does not exist"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public CustomProjectMemberResponse getMemberId(Integer userId,Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("project with id " + projectId + " does not exist"));

        ProjectMember projectMember = projectMemberRepository.findProjectMemberByUserIdAndProject(userId,project)
                .orElseThrow(() -> new NotFoundException("user with id " + userId + " does not exist in project with id "+projectId));

        return new CustomProjectMemberResponse(
                projectMember.getId(),
                projectMember.getProject().getAdminEmail()
        );
    }
    public boolean checkIfAdmin(Integer userId,Integer projectId){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new NotFoundException("project not found"));

        Optional<ProjectMember> admin = projectMemberRepository.findByProjectAndUserIdAndRole(project,userId,MemberRole.ADMIN);
        return admin.isPresent();
    }
}
