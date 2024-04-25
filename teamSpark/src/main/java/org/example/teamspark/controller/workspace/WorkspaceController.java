package org.example.teamspark.controller.workspace;

import org.example.teamspark.data.DataResponse;
import org.example.teamspark.data.dto.WorkspaceDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("api/v1/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @Autowired
    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getUserWorkspaces() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<WorkspaceDto> workspaceDtoList = workspaceService.getWorkspacesWithMembersByUser(user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(workspaceDtoList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkspaceById(@PathVariable Long id) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        WorkspaceDto workspaceDto = workspaceService.getWorkspaceById(user, id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(workspaceDto));
    }

    @PostMapping(value = "/", consumes = {"application/json"})
    public ResponseEntity<?> createWorkspace(@RequestBody WorkspaceDto workspaceDto) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long savedWorkspaceId = workspaceService.createWorkspace(user, workspaceDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new DataResponse<>(Map.of("id", savedWorkspaceId)));
    }

    @PutMapping(value = "/{id}", consumes = {"application/json"})
    public ResponseEntity<?> updateWorkspace(@PathVariable Long id, @RequestBody WorkspaceDto workspaceDto) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        workspaceService.updateWorkspace(id, user, workspaceDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteWorkspace(@PathVariable Long id) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        workspaceService.deleteWorkspace(id, user);

        return ResponseEntity.ok().build();
    }

}
