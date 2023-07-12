package com.example.projectservice.project;

import com.example.helpers.exceptions.BadRequestException;
import com.example.helpers.exceptions.NotFoundException;
import com.example.helpers.exceptions.UnauthorizedException;
import com.example.projectservice.projectmember.ProjectMember;
import com.example.projectservice.projectmember.ProjectMemberCreationRequest;
import com.example.projectservice.projectmember.ProjectMemberRepository;
import com.example.projectservice.projectmember.ProjectMemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberService projectMemberService;

    public List<Project> getUserProjects(Integer userId) {
        List<Project> projects = projectRepository.findByAdminId(userId);

        for (Project project : projects) {
            List<ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
            project.setMembers(new HashSet<>(members));
        }

        return projects;
    }

    public Project getProject(Integer id,Integer userId) {
        Project project = projectRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("project with id " + id + " does not exist"));

        Optional<ProjectMember> projectMember = projectMemberRepository.findProjectMemberByUserIdAndProject(userId,project);

        //todo check if project is private: version2
        if(projectMember.isEmpty()) throw new UnauthorizedException("this Project is private, you must be a member to view details");
        List<ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
        project.setMembers(new HashSet<>(members));

        return project;
    }

    public Project createProject(Integer userId, String username, ProjectCreationRequest request) {
        try {
            Project project = Project.builder()
                    .adminId(userId)
                    .adminEmail(username)
                    .title(request.title())
                    .description(request.description())
                    .isPublic(request.isPublic())
                    .build();
            projectRepository.saveAndFlush(project);

            //Add admin to project members
            ProjectMemberCreationRequest _request = new ProjectMemberCreationRequest(project.getId(),userId);
            projectMemberService.addUserToProject(_request);

            List<ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
            project.setMembers(new HashSet<>(members));

            return project;
        } catch (Exception e) {
            throw new BadRequestException("Your request is not correct");
        }

    }

    public Project updateProject(Integer id,Integer userId, ProjectUpdateRequest request) {
        Project project = projectRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("project with id " + id + " does not exist"));

        if(!Objects.equals(userId, project.getAdminId())) throw new UnauthorizedException("You are not permited to do this operation");

        try {
            project.setTitle(request.title());
            project.setDescription(request.description());
            project.setIsPublic(request.isPublic());
            projectRepository.save(project);

            List<ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
            project.setMembers(new HashSet<>(members));
            return project;
        } catch (Exception e) {
            throw new BadRequestException("Your request is not correct");
        }
    }

    public String deleteProject(Integer id,Integer userId) {
        Project project = projectRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("project with id " + id + " does not exist"));

        if(!Objects.equals(userId, project.getAdminId())) throw new UnauthorizedException("You are not permited to do this operation");

        projectRepository.delete(project);
        return "project deleted!";
    }

    public Boolean checkIfAdmin(Integer projectId, Integer userId) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException("project with id " + projectId + " does not exist"));


        return Objects.equals(userId, project.getAdminId());
    }

    public List<Project> getProjects() {
        List<Project> projects = projectRepository.findAll();

        for (Project project : projects) {
            List<ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
            project.setMembers(new HashSet<>(members));
        }

        return projects;
    }
}
