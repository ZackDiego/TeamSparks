package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.teamspark.data.dto.UserDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.Workspace;
import org.example.teamspark.model.workspace.WorkspaceMember;
import org.example.teamspark.repository.UserRepository;
import org.example.teamspark.repository.WorkspaceMemberRepository;
import org.example.teamspark.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceMemberService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    public WorkspaceMemberService(WorkspaceRepository workspaceRepository,
                                  WorkspaceMemberRepository workspaceMemberRepository,
                                  UserRepository userRepository) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
    }

    public void addWorkspaceMember(User user,
                                   Long workspaceId,
                                   UserDto userDto) throws ResourceAccessDeniedException {

        // Find the workspace by id
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found with ID: " + workspaceId));

        // Check if user owns the workspace
        if (!workspace.getCreator().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        // Create new channelMember instance
        WorkspaceMember workspaceMember = new WorkspaceMember();

        User addUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userDto.getId()));

        workspaceMember.setWorkspace(workspace);
        workspaceMember.setUser(addUser);
        workspaceMemberRepository.save(workspaceMember);
    }

    public void removeWorkspaceMember(User user,
                                      Long workspaceId,
                                      Long memberId) throws ResourceAccessDeniedException {
        // Find the workspace by id
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found with ID: " + workspaceId));

        // Check if user owns the workspace
        if (!workspace.getCreator().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        // Find the workspaceMember by memberId
        WorkspaceMember workspaceMember = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + memberId));

        workspaceMemberRepository.delete(workspaceMember);
    }
}
