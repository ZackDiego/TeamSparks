package org.example.teamspark.controller.workspace;

import org.example.teamspark.data.DataResponse;
import org.example.teamspark.data.dto.RoleDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.WorkspaceRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api/v1/workspace/{workspaceId}/role")
public class WorkspaceRoleController {

    private final WorkspaceRoleService workspaceRoleService;

    @Autowired
    public WorkspaceRoleController(WorkspaceRoleService workspaceRoleService) {
        this.workspaceRoleService = workspaceRoleService;
    }

    // create role
    @PostMapping(value = "", consumes = {"application/json"})
    public ResponseEntity<?> createWorkspaceRole(@PathVariable Long workspaceId, @RequestBody RoleDto roleDto) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        RoleDto createdRoleDto = workspaceRoleService.createWorkspacesRole(workspaceId, user, roleDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(createdRoleDto));
    }

    // read role
    @GetMapping("")
    public ResponseEntity<?> getWorkspaceRoles(@PathVariable Long workspaceId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<RoleDto> roleDtos = workspaceRoleService.getWorkspacesRoles(workspaceId, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(roleDtos));
    }


//    // update role (assign role to member)
//    @PostMapping(value = "/", consumes = {"application/json"})
//    public ResponseEntity<?> createWorkspace(@RequestBody WorkspaceDto workspaceDto) {
//
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        WorkspaceDto createdWorkspace = workspaceService.createWorkspace(user, workspaceDto);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(new DataResponse<>(createdWorkspace));
//    }

    // update role
    @PutMapping(value = "/{roleId}", consumes = {"application/json"})
    public ResponseEntity<?> updateWorkspace(@PathVariable Long workspaceId, @PathVariable Long roleId,
                                             @RequestBody RoleDto roleDto) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        RoleDto updatedRoleDto = workspaceRoleService.updateWorkspaceRole(workspaceId, roleId, roleDto, user);

        return ResponseEntity.ok().body(new DataResponse<>(updatedRoleDto));
    }

    // delete role
    @DeleteMapping(value = "/{roleId}")
    public ResponseEntity<?> deleteWorkspace(@PathVariable Long workspaceId, @PathVariable Long roleId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        workspaceRoleService.deleteWorkspaceRole(workspaceId, roleId, user);

        return ResponseEntity.ok().build();
    }

}
