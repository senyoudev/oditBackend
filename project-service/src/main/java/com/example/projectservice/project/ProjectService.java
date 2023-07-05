package com.example.projectservice.project;

import com.example.helpers.exceptions.BadRequestException;
import com.example.helpers.exceptions.NotFoundException;
import com.example.projectservice.projectmember.ProjectMember;
import com.example.projectservice.projectmember.ProjectMemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public List<Project> getUserProjects(Integer userId){
        List<Project> projects = projectRepository.findByAdminId(userId);

        for(Project project: projects){
            List<ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
            project.setMembers(new HashSet<>(members));
        }

        return projects;
    }
    public Project getProject(Integer id){
        Project project = projectRepository
                .findById(id)
                .orElseThrow(()->new NotFoundException("project with id "+id+" does not exist"));

        List<ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
        project.setMembers(new HashSet<>(members));

        return project;
    }
    public Project createProject(Integer userId,String username,ProjectCreationRequest request) {
        try{
            Project project = Project.builder()
                    .adminId(userId)
                    .adminEmail(username)
                    .title(request.title())
                    .description(request.description())
                    .isPublic(request.isPublic())
                    .build();

            projectRepository.saveAndFlush(project);
            return project;
        }catch (Exception e){
            throw new BadRequestException("Your request is not correct");
        }

    }

    public Project updateProject(Integer id,ProjectUpdateRequest request) {
        Project project = projectRepository
                .findById(id)
                .orElseThrow(()->new NotFoundException("project with id {id} does not exist"));
        try{
            project.setTitle(request.title());
            project.setDescription(request.description());
            project.setIsPublic(request.isPublic());
            projectRepository.save(project);

            List<ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
            project.setMembers(new HashSet<>(members));
            return project;
        }catch (Exception e){
            throw new BadRequestException("Your request is not correct");
        }
    }

    public String deleteProject(Integer id){
        Project project = projectRepository
                .findById(id)
                .orElseThrow(()->new NotFoundException("project with id {id} does not exist"));
        projectRepository.delete(project);
        return "project deleted!";
    }

    public List<Project> getProjects() {
        List<Project> projects = projectRepository.findAll();

        for(Project project: projects){
            List<ProjectMember> members = projectMemberRepository.findProjectMembersByProject(project);
            project.setMembers(new HashSet<>(members));
        }

        return projects;
    }
}
