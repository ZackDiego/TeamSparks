package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.teamspark.data.dto.UserDto;
import org.example.teamspark.data.dto.WorkspaceDto;
import org.example.teamspark.data.dto.WorkspaceMemberDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.Workspace;
import org.example.teamspark.model.workspace.WorkspaceMember;
import org.example.teamspark.repository.UserRepository;
import org.example.teamspark.repository.WorkspaceMemberRepository;
import org.example.teamspark.repository.WorkspaceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public WorkspaceService(WorkspaceRepository workspaceRepository, UserRepository userRepository, WorkspaceMemberRepository workspaceMemberRepository, ModelMapper modelMapper) {
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.modelMapper = modelMapper;
    }

    private static List<WorkspaceDto> mapResultSetToWorkspaceDtos(List<Object[]> rs) {
        // Group the results by workspace ID
        Map<Long, List<Object[]>> groupedResults = rs.stream()
                .collect(Collectors.groupingBy(row -> (Long) row[0]));

        // Map the grouped results to WorkspaceDto objects
        List<WorkspaceDto> workspaceDtos = groupedResults.entrySet().stream()
                .map(entry -> {
                    WorkspaceDto workspaceDto = new WorkspaceDto();
                    workspaceDto.setId(entry.getKey());
                    workspaceDto.setName((String) entry.getValue().get(0)[1]);
                    workspaceDto.setCreatedAt((Date) entry.getValue().get(0)[2]);
                    workspaceDto.setAvatar((String) entry.getValue().get(0)[3]);

                    // workspace creator
                    WorkspaceMemberDto creatorDto = new WorkspaceMemberDto();
                    creatorDto.setId((Long) entry.getValue().get(0)[4]);
                    UserDto creatorUserDto = new UserDto();
                    creatorUserDto.setId((Long) entry.getValue().get(0)[5]);
                    creatorUserDto.setName((String) entry.getValue().get(0)[6]);
                    creatorUserDto.setAvatar((String) entry.getValue().get(0)[7]);
                    creatorDto.setUserDto(creatorUserDto);
                    workspaceDto.setCreator(creatorDto);

                    List<WorkspaceMemberDto> members = entry.getValue().stream()
                            .map(row -> {
                                WorkspaceMemberDto memberDto = new WorkspaceMemberDto();
                                memberDto.setId((Long) row[8]);

                                // Map user details to UserDto
                                UserDto userDto = new UserDto();
                                userDto.setId((Long) row[9]);
                                userDto.setName((String) row[10]);
                                userDto.setAvatar((String) row[11]);

                                memberDto.setUserDto(userDto);

                                return memberDto;
                            }).collect(Collectors.toList());

                    workspaceDto.setMembers(members);
                    return workspaceDto;
                }).collect(Collectors.toList());
        return workspaceDtos;
    }

    @Transactional
    public void createWorkspace(User creator, WorkspaceDto dto) {

        Workspace workspace = modelMapper.map(dto, Workspace.class);

        workspace.setCreator(creator);
        workspace.setCreatedAt(new Date());

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        // Create new workspace member to save
        WorkspaceMember newMember = new WorkspaceMember();
        newMember.setWorkspace(workspace);
        newMember.setUser(creator);
        workspaceMemberRepository.save(newMember);
    }

    public List<WorkspaceDto> getWorkspacesWithMembersByUser(User user) {

        List<Object[]> rs = workspaceRepository.findWorkspaceWithMembersByUserId(user.getId());

        List<WorkspaceDto> workspaceDtos = mapResultSetToWorkspaceDtos(rs);

        return workspaceDtos;
    }

    public WorkspaceDto getWorkspaceById(User user, Long id) throws ResourceAccessDeniedException {

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        // check if user owns the workspace
        if (!workspace.getCreator().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }


        List<Object[]> rs = workspaceRepository.findWorkspaceWithMembersById(id);

        return mapResultSetToWorkspaceDtos(rs).get(0);
    }

    @Transactional
    public void updateWorkspace(Long id, User user, WorkspaceDto dto) throws ResourceAccessDeniedException {

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        // check if user owns the workspace
        if (!workspace.getCreator().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        // convert members to memberId
        Set<Long> updatedUserIdsSet = dto.getMembers().stream()
                .map(WorkspaceMemberDto::getId)
                .collect(Collectors.toSet());

        // Update members
        updateWorkspaceMembers(workspace, updatedUserIdsSet);

        // Update the fields of the workspace entity
        workspace.setName(dto.getName());
        workspace.setAvatar(dto.getAvatar());

        // Save the updated workspace entity back to the repository
        workspaceRepository.save(workspace);
    }

    public void updateWorkspaceMembers(Workspace workspace, Set<Long> updatedUserIds) {

        // Retrieve existing members
        Set<User> currentMembers = workspaceMemberRepository.findByWorkspace(workspace).stream()
                .map(WorkspaceMember::getUser)
                .collect(Collectors.toSet());

        // Find workspace members to add
        Set<Long> userIdsToAdd = updatedUserIds.stream()
                .filter(userId -> currentMembers.stream()
                        .noneMatch(member -> member.getId().equals(userId)))
                .collect(Collectors.toSet());

        // Find workspace members to remove
        Set<Long> userIdsToRemove = currentMembers.stream()
                .map(User::getId)
                .filter(id -> !updatedUserIds.contains(id))
                .collect(Collectors.toSet());

        // Add new workspace members
        for (Long userId : userIdsToAdd) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

            WorkspaceMember newMember = new WorkspaceMember();
            newMember.setWorkspace(workspace);
            newMember.setUser(user);
            workspaceMemberRepository.save(newMember);
        }

        // Remove existing workspace members
        for (Long userId : userIdsToRemove) {
            currentMembers.removeIf(member -> member.getId().equals(userId));
            workspaceMemberRepository.deleteByWorkspaceIdAndUserId(workspace.getId(), userId);
        }
    }

    @Transactional
    public void deleteWorkspace(Long id, User user) throws ResourceAccessDeniedException {

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        // check if user owns the workspace
        if (!workspace.getCreator().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        // Delete associated WorkspaceMember records
        workspaceMemberRepository.deleteByWorkspace(workspace);

        workspaceRepository.deleteById(id);
    }
}
