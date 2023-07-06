package com.example.projectservice.projectmember;

import com.example.helpers.exceptions.BadRequestException;
import com.example.helpers.exceptions.NotFoundException;
import com.example.helpers.exceptions.UnauthorizedException;
import com.example.projectservice.project.Project;
import com.example.projectservice.project.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    public List<ProjectMember> getProjectMembers(Integer projectId,Integer userId){
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(()->new NotFoundException("project with id "+projectId+" does not exist"));

        if(!project.getIsPublic()){
            projectMemberRepository.findProjectMemberByUserId(userId)
                    .orElseThrow(()->new UnauthorizedException("You must be a member in this project"));
        }

        List <ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
        return members;
    }
    public ProjectMember getProjectMember(Integer id,Integer userId){
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(()->new NotFoundException("member with id {id} does not exist"));
        if(userId != member.getUserId()) throw new UnauthorizedException("You must be a member in this project");
        return member;
    }
    public ProjectMember addUserToProject(ProjectMemberCreationRequest request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(()->new NotFoundException("project with id {id} does not exist"));
        try{
            ProjectMember member = ProjectMember.builder()
                    .project(project)
                    .userId(request.userId())
                    .role(MemberRole.MEMBER)
                    .build();

            projectMemberRepository.saveAndFlush(member);
            return member;
        }catch (Exception e){
            throw new BadRequestException("Your request is not correct");
        }
    }

    public ProjectMember updateProjectMember(Integer id,Integer userId,ProjectMemberUpdateRequest request) {
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(()->new NotFoundException("member with id "+id+ "does not exist"));
        if(userId != member.getProject().getAdminId()) new UnauthorizedException("You are not permited to do this operation");
        try{
            member.setRole(request.role());
            projectMemberRepository.save(member);

            return member;
        }catch (Exception e){
            throw new BadRequestException("Role is required!");
        }
    }

    public String removeMemberFromProject(Integer id,Integer userId){
        ProjectMember member = projectMemberRepository
                .findById(id)
                .orElseThrow(()->new NotFoundException("member with id "+id +" does not exist"));
        if(userId != member.getProject().getAdminId()) new UnauthorizedException("You are not permited to do this operation");
        projectMemberRepository.delete(member);
        return "Member Removed from project";
    }

    public Boolean checkIfMember(Integer memberId) {
        try {
            projectMemberRepository
                    .findProjectMemberByUserId(memberId).orElseThrow(()->new NotFoundException("member with id "+memberId +" does not exist"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
