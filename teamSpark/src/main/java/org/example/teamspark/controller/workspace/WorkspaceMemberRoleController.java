package org.example.teamspark.controller.workspace;

import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.WorkspaceMemberRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/v1/workspace/{workspaceId}")
public class WorkspaceMemberRoleController {

    private final WorkspaceMemberRoleService workspaceMemberRoleService;

    @Autowired
    public WorkspaceMemberRoleController(WorkspaceMemberRoleService workspaceMemberRoleService) {
        this.workspaceMemberRoleService = workspaceMemberRoleService;
    }

    // assign workspace member roles
    @PostMapping(value = "/member/{memberId}/role/{roleId}", consumes = {"application/json"})
    public ResponseEntity<?> assignWorkspaceMemberRoles(
            @PathVariable Long workspaceId,
            @PathVariable Long memberId,
            @PathVariable Long roleId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        workspaceMemberRoleService.assignWorkspaceMemberRole(workspaceId, user, memberId, roleId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // remove workspace member role
    @DeleteMapping("/member/{memberId}/role/{roleId}")
    public ResponseEntity<?> removeWorkspaceMembersRole(
            @PathVariable Long workspaceId,
            @PathVariable Long memberId,
            @PathVariable Long roleId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        workspaceMemberRoleService.removeWorkspaceMemberRole(workspaceId, user, memberId, roleId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
