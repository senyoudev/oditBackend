package com.example.projectservice.project;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> getUserProject(Integer userId){
        List<Project> projects = projectRepository.findByUserId(userId);
        return projects;
    }
    public Project getProject(Integer id){
        Project project = projectRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));
        return project;
    }
    public Project createProject(Integer userId,ProjectCreationRequest request) {
        Project project = Project.builder()
                .userId(userId)
                .title(request.title())
                .description(request.description())
                .isPublic(request.isPublic())
                .build();

        projectRepository.saveAndFlush(project);
        return project;
    }

    public Project updateProject(Integer id,ProjectUpdateRequest request) {
        Project project = projectRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));

        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setIsPublic(request.isPublic());
        projectRepository.save(project);
        return project;

    }

    public String deleteProject(Integer id){
        Project project = projectRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));
        projectRepository.delete(project);
        return "project deleted!";
    }
}
