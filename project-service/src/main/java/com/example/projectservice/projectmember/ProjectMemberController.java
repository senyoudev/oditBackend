package com.example.projectservice.projectmember;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.helpers.projects.CustomProjectMemberResponse;

import java.util.List;

@RestController
@RequestMapping("api/v1/projectmembers")
@AllArgsConstructor
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;

    @GetMapping
    public List<ProjectMember> getProjectMembers(@RequestParam Integer userId,@RequestParam Integer projectId) {
        return projectMemberService.getProjectMembers(projectId,userId);
    }

    @GetMapping(value = "{id}")
    public ProjectMember getProjectMember(@PathVariable("id") Integer id,@RequestParam Integer userId) {
        return projectMemberService.getProjectMember(id,userId);
    }

    //Just for testing
    @PostMapping
    public void addUserToProject(@RequestBody ProjectMemberCreationRequest request){
        projectMemberService.addUserToProject(request,MemberRole.MEMBER);
    }
    @PutMapping(value = "{id}")
    public ProjectMember updateProjectMember(@RequestParam Integer adminId,@PathVariable("id") Integer id,@RequestBody ProjectMemberUpdateRequest request){
        return projectMemberService.updateProjectMember(id,adminId,request);
    }

    @DeleteMapping(value = "{id}")
    public String removeMemberFromProject(@RequestParam Integer adminId,@PathVariable("id") Integer id){
        return projectMemberService.removeMemberFromProject(id,adminId);
    }

    //Todo remove this endpoints from gateway (used only by services)
    @GetMapping("/checkifmember")
    public Boolean checkIfMember(@RequestParam Integer memberId) {
        return projectMemberService.checkIfMember(memberId);
    }
    @GetMapping("/checkIfAdmin")
    public Boolean checkIfAdmin(@RequestParam Integer userId,@RequestParam Integer projectId) {
        return projectMemberService.checkIfAdmin(userId,projectId);
    }

    @GetMapping("/getMemberId")
    public CustomProjectMemberResponse getMemberId(@RequestParam Integer userId,@RequestParam Integer projectId) {
        return projectMemberService.getMemberId(userId,projectId);
    }
}
