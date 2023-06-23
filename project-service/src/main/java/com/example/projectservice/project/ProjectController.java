package com.example.projectservice.project;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/projects")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<Project> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping(value = "{id}")
    public Project getProject(@PathVariable("id") Integer id) {
        return projectService.getProject(id);
    }

    @PostMapping
    public void createProject(@RequestBody ProjectCreationRequest projectCreationRequest){
        projectService.createProject(projectCreationRequest);
    }

    @PutMapping(value = "{id}")
    public void updateProject(@PathVariable("id") Integer id,@RequestBody ProjectUpdateRequest projectUpdateRequest){
        projectService.updateProject(id,projectUpdateRequest);
    }

    @DeleteMapping(value = "{id}")
    public void updateProject(@PathVariable("id") Integer id){
        projectService.deleteProject(id);
    }
}
