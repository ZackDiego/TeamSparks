package org.example.teamspark.controller.workspace;

import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.WorkspaceMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("api/v1/workspace/{workspaceId}/member")
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;

    @Autowired
    public WorkspaceMemberController(WorkspaceMemberService channelMemberService) {
        this.workspaceMemberService = channelMemberService;
    }

    // add workspace member
    @PostMapping(value = "")
    public ResponseEntity<?> handleAddWorkspaceMember(
            @PathVariable Long workspaceId,
            @RequestParam Long userId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long savedWorkspaceId = workspaceMemberService.addWorkspaceMember(user, workspaceId, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", savedWorkspaceId));
    }

    // remove workspace member
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> handleRemoveWorkspaceMember(
            @PathVariable Long workspaceId,
            @PathVariable Long memberId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        workspaceMemberService.removeWorkspaceMember(user, workspaceId, memberId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
