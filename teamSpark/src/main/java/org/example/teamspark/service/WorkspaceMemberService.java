package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
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
//    private final EmailNotificationService emailNotificationService;

    public WorkspaceMemberService(WorkspaceRepository workspaceRepository,
                                  WorkspaceMemberRepository workspaceMemberRepository,
                                  UserRepository userRepository) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
    }

    public Long addWorkspaceMember(User user,
                                   Long workspaceId,
                                   Long userId) throws ResourceAccessDeniedException {

        // Find the workspace by id
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found with ID: " + workspaceId));

        // Check if user owns the workspace
        WorkspaceMember creator = workspaceMemberRepository.findCreatorByWorkspaceId(workspaceId);

        if (!creator.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        // Create new channelMember instance
        WorkspaceMember workspaceMember = new WorkspaceMember();

        User addUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        workspaceMember.setWorkspace(workspace);
        workspaceMember.setUser(addUser);
        WorkspaceMember savedWorkspaceMember = workspaceMemberRepository.save(workspaceMember);

        // To do: Send email notification
//        emailNotificationService.sendWorkspaceMemberInviteEmail(addUser, workspace);

        return savedWorkspaceMember.getId();
    }

    public void removeWorkspaceMember(User user,
                                      Long workspaceId,
                                      Long memberId) throws ResourceAccessDeniedException {

        // Check if user owns the workspace
        WorkspaceMember creator = workspaceMemberRepository.findCreatorByWorkspaceId(workspaceId);

        if (!creator.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        // Find the workspaceMember by memberId
        WorkspaceMember workspaceMember = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + memberId));

        workspaceMemberRepository.delete(workspaceMember);
    }
}
