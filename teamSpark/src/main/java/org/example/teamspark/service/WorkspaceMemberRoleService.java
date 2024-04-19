package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.teamspark.data.dto.RoleDto;
import org.example.teamspark.data.dto.WorkspaceMemberDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.Workspace;
import org.example.teamspark.model.workspace.WorkspaceMember;
import org.example.teamspark.model.workspace.role.MemberRole;
import org.example.teamspark.model.workspace.role.Role;
import org.example.teamspark.repository.MemberRoleRepository;
import org.example.teamspark.repository.RoleRepository;
import org.example.teamspark.repository.WorkspaceMemberRepository;
import org.example.teamspark.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkspaceMemberRoleService {
    private final WorkspaceRepository workspaceRepository;

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final RoleRepository roleRepository;
    private final MemberRoleRepository memberRoleRepository;

    @Autowired
    public WorkspaceMemberRoleService(WorkspaceRepository workspaceRepository, WorkspaceMemberRepository workspaceMemberRepository,
                                      RoleRepository roleRepository, MemberRoleRepository workspaceMemberRoleRepository) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.roleRepository = roleRepository;
        this.memberRoleRepository = workspaceMemberRoleRepository;
    }

    @Transactional
    public WorkspaceMemberDto assignWorkspaceMemberRole(Long workspaceId, User user, Long memberId, Long roleId)
            throws ResourceAccessDeniedException {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        // check if user owns the workspace
        if (!workspace.getCreator().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        // Retrieve the Role entity
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role with id " + roleId + " not found"));

        // Retrieve the WorkspaceMember entity
        WorkspaceMember workspaceMember = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member with id " + memberId + " not found"));

        MemberRole memberRole = new MemberRole();
        memberRole.setRole(role);
        memberRole.setWorkspaceMember(workspaceMember);

        // Save the new MemberRole entity
        memberRoleRepository.save(memberRole);

        List<Object[]> result = workspaceMemberRepository.findWorkspaceMemberDtoById(memberId);

        WorkspaceMemberDto assignedMemberDto = null;
        for (Object[] row : result) {

            if (assignedMemberDto == null) {
                assignedMemberDto = new WorkspaceMemberDto();
                assignedMemberDto.setId((Long) row[0]);
                assignedMemberDto.setName((String) row[1]);
                assignedMemberDto.setAvatar((String) row[2]);
                assignedMemberDto.setRoles(new ArrayList<>()); // Initialize roles list
            }

            RoleDto roleDto = new RoleDto();
            roleDto.setId((Long) row[3]);
            roleDto.setName((String) row[4]);
            roleDto.setGroupByRole((Boolean) row[5]);

            assignedMemberDto.getRoles().add(roleDto);
        }
        return assignedMemberDto;
    }

    public void removeWorkspaceMemberRole(Long workspaceId, User user, Long memberId, Long roleId)
            throws ResourceAccessDeniedException {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        // check if user owns the workspace
        if (!workspace.getCreator().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        // Query the database to verify that the roleId matches the memberId
        MemberRole memberRole = memberRoleRepository.findByWorkspaceMemberIdAndRoleId(memberId, roleId);

        // Check if the member role exists
        if (memberRole == null) {
            throw new EntityNotFoundException("Member with ID " + memberId + " is not assigned to role with ID " + roleId);
        }

        // Delete the member role
        memberRoleRepository.delete(memberRole);
    }
}
