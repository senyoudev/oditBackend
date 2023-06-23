package com.example.projectservice.projectmember;

import com.example.projectservice.project.Project;
import com.example.projectservice.project.ProjectCreationRequest;
import com.example.projectservice.project.ProjectService;
import com.example.projectservice.project.ProjectUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/projectmembers")
@AllArgsConstructor
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;

    @GetMapping
    public List<ProjectMember> getProjectMembers() {
        return projectMemberService.getProjectMembers();
    }

    @GetMapping(value = "{id}")
    public ProjectMember getProjectMember(@PathVariable("id") Integer id) {
        return projectMemberService.getProjectMember(id);
    }

    @PostMapping
    public void addUserToProject(@RequestBody ProjectMemberCreationRequest request){
        projectMemberService.addUserToProject(request);
    }

    @PutMapping(value = "{id}")
    public void updateProjectMember(@PathVariable("id") Integer id,@RequestBody ProjectMemberUpdateRequest request){
        projectMemberService.updateProjectMember(id,request);
    }

    @DeleteMapping(value = "{id}")
    public void removeMemberFromProject(@PathVariable("id") Integer id){
        projectMemberService.removeMemberFromProject(id);
    }
}
