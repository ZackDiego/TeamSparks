package org.example.teamspark.controller.workspace;

import org.example.teamspark.data.dto.UserDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.WorkspaceMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/v1/workspace/{workspace_id}/member")
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;

    @Autowired
    public WorkspaceMemberController(WorkspaceMemberService channelMemberService) {
        this.workspaceMemberService = channelMemberService;
    }

    // add channel member
    @PostMapping(value = "", consumes = {"application/json"})
    public ResponseEntity<?> handleCreateChannel(
            @PathVariable("workspace_id") Long workspaceId,
            @RequestBody UserDto userDto) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        workspaceMemberService.addWorkspaceMember(user, workspaceId, userDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // remove channel member
    @DeleteMapping("/{member_id}")
    public ResponseEntity<?> handleDeleteChannel(
            @PathVariable("workspace_id") Long workspaceId,
            @PathVariable("member_id") Long memberId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        workspaceMemberService.removeWorkspaceMember(user, workspaceId, memberId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
