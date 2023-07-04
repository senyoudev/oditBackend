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
    public List<Project> getUserProjects(@RequestParam Integer userId) {
        return projectService.getUserProjects(userId);
    }

    @GetMapping(value = "{id}")
    public Project getProject(@PathVariable("id") Integer id) {
        return projectService.getProject(id);
    }

    @PostMapping
    public Project createProject(@RequestParam Integer userId,@RequestParam String username,@RequestBody ProjectCreationRequest projectCreationRequest){
        return projectService.createProject(userId,username,projectCreationRequest);
    }

    @PutMapping(value = "{id}")
    public Project updateProject(@PathVariable("id") Integer id,@RequestBody ProjectUpdateRequest projectUpdateRequest){
       return projectService.updateProject(id,projectUpdateRequest);
    }

    @DeleteMapping(value = "{id}")
    public String updateProject(@PathVariable("id") Integer id){
        return projectService.deleteProject(id);
    }
}
