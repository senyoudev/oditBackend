package com.example.projectservice.project;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    public List<Project> getProjects(){
        return projectRepository.findAll();
    }

    public Project getProject(Integer id){
        Project project = projectRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));
        return project;
    }
    public void createProject(ProjectCreationRequest request) {
        Project project = Project.builder()
                .owner(request.owner())
                .title(request.title())
                .description(request.description())
                .isPublic(request.isPublic())
                .build();

        projectRepository.saveAndFlush(project);
    }

    public void updateProject(Integer id,ProjectUpdateRequest request) {
        Project project = projectRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));

        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setIsPublic(request.isPublic());
        projectRepository.save(project);
    }

    public void deleteProject(Integer id){
        Project project = projectRepository
                .findById(id)
                .orElseThrow(()->new IllegalStateException("project with id {id} does not exist"));
        projectRepository.delete(project);
    }
}
