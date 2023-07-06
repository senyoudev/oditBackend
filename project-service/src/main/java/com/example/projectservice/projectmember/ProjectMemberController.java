package com.example.projectservice.projectmember;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/projectmembers")
@AllArgsConstructor
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;

    @GetMapping
    public List<ProjectMember> getProjectMembers(@RequestParam Integer projectId) {
        return projectMemberService.getProjectMembers(projectId);
    }

    @GetMapping(value = "{id}")
    public ProjectMember getProjectMember(@PathVariable("id") Integer id) {
        return projectMemberService.getProjectMember(id);
    }

    @PostMapping
    public ProjectMember addUserToProject(@RequestParam Integer adminId,@RequestBody ProjectMemberCreationRequest request){
        return projectMemberService.addUserToProject(adminId,request);
    }

    @PutMapping(value = "{id}")
    public ProjectMember updateProjectMember(@RequestParam Integer adminId,@PathVariable("id") Integer id,@RequestBody ProjectMemberUpdateRequest request){
        return projectMemberService.updateProjectMember(id,adminId,request);
    }

    @DeleteMapping(value = "{id}")
    public String removeMemberFromProject(@RequestParam Integer adminId,@PathVariable("id") Integer id){
        return projectMemberService.removeMemberFromProject(id,adminId);
    }

    @GetMapping("/checkifmember")
    public Boolean checkIfMember(@RequestParam Integer memberId) {
        return projectMemberService.checkIfMember(memberId);
    }
}
