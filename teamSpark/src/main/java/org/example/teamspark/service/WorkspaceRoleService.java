package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.teamspark.data.dto.RoleDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.Workspace;
import org.example.teamspark.model.workspace.WorkspaceMember;
import org.example.teamspark.model.workspace.role.Role;
import org.example.teamspark.repository.RoleRepository;
import org.example.teamspark.repository.WorkspaceMemberRepository;
import org.example.teamspark.repository.WorkspaceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkspaceRoleService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public WorkspaceRoleService(WorkspaceRepository workspaceRepository, WorkspaceMemberRepository workspaceMemberRepository,
                                RoleRepository roleRepository, ModelMapper modelMapper) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    public RoleDto createWorkspacesRole(Long workspaceId, User user, RoleDto roleDto) throws ResourceAccessDeniedException {

        // Find the workspace by id
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found with ID: " + workspaceId));

        // Check if user owns the workspace
        WorkspaceMember creator = workspaceMemberRepository.findCreatorByWorkspaceId(workspaceId);

        if (!creator.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        Role role = modelMapper.map(roleDto, Role.class);
        role.setWorkspace(workspace);

        Role createdRole = roleRepository.save(role);

        return modelMapper.map(createdRole, RoleDto.class);
    }

    public List<RoleDto> getWorkspacesRoles(Long workspaceId, User user) throws ResourceAccessDeniedException {

        // Find the workspace by id
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found with ID: " + workspaceId));

        // Check if user owns the workspace
        WorkspaceMember creator = workspaceMemberRepository.findCreatorByWorkspaceId(workspaceId);

        if (!creator.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        List<Role> roles = roleRepository.findByWorkspace(workspace);

        return roles.stream()
                .map(role -> modelMapper.map(role, RoleDto.class))
                .collect(Collectors.toList());
    }

    public void deleteWorkspaceRole(Long workspaceId, Long roleId, User user) throws ResourceAccessDeniedException {

        // Find the workspace by id
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found with ID: " + workspaceId));

        // Check if user owns the workspace
        WorkspaceMember creator = workspaceMemberRepository.findCreatorByWorkspaceId(workspaceId);

        if (!creator.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        roleRepository.deleteById(roleId);
    }

    public RoleDto updateWorkspaceRole(Long workspaceId, Long roleId, RoleDto roleDto, User user) throws ResourceAccessDeniedException {

        // Find the workspace by id
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found with ID: " + workspaceId));

        // Check if user owns the workspace
        WorkspaceMember creator = workspaceMemberRepository.findCreatorByWorkspaceId(workspaceId);

        if (!creator.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("RoleId " + roleId + " not found"));

        // Check if role is under the correct workspace
        if (!role.getWorkspace().getId().equals(workspace.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the role");
        }

        // Update role
        role.setName(roleDto.getName());
        role.setGroupByRole(roleDto.isGroupByRole());
        roleRepository.save(role);

        return modelMapper.map(role, RoleDto.class);
    }
}
