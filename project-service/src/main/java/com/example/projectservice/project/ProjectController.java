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
    public Project updateProject(@PathVariable("id") Integer id,@RequestParam Integer userId,@RequestBody ProjectUpdateRequest projectUpdateRequest){
       return projectService.updateProject(id,userId,projectUpdateRequest);
    }

    @DeleteMapping(value = "{id}")
    public String deleteProject(@PathVariable("id") Integer id,@RequestParam Integer userId){
        return projectService.deleteProject(id,userId);
    }

    @GetMapping("/checkifadmin")
    public Boolean checkIfAdmin(@RequestParam Integer projectId,@RequestParam Integer userId) {
        return projectService.checkIfAdmin(projectId,userId);
    }
}
