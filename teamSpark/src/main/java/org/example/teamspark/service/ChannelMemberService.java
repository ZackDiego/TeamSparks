package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.teamspark.data.dto.WorkspaceMemberDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.channel.Channel;
import org.example.teamspark.model.channel.ChannelMember;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.WorkspaceMember;
import org.example.teamspark.repository.ChannelMemberRepository;
import org.example.teamspark.repository.ChannelRepository;
import org.example.teamspark.repository.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class ChannelMemberService {
    private final ChannelRepository channelRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ChannelMemberRepository channelMemberRepository;

    public ChannelMemberService(ChannelRepository channelRepository, WorkspaceMemberRepository workspaceMemberRepository, ChannelMemberRepository channelMemberRepository) {
        this.channelRepository = channelRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.channelMemberRepository = channelMemberRepository;
    }

    public void addChannelMember(User user,
                                 Long creatorId,
                                 Long channelId,
                                 WorkspaceMemberDto workspaceMemberDto) throws ResourceAccessDeniedException {

        // Find the creator by creatorId
        WorkspaceMember creator = workspaceMemberRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + creatorId));

        // Check if the WorkspaceMember belongs to the provided user
        if (!creator.getUser().equals(user)) {
            throw new ResourceAccessDeniedException("User is unauthorized to access channels for workspace member with ID " + creatorId);
        }

        // Find the Channel by channelId
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));

        // Check if the member is the channel's creator
        if (!channel.getCreator().getId().equals(creatorId)) {
            throw new ResourceAccessDeniedException("User is unauthorized to update channel with ID " + channelId);
        }


        // Create new channelMember instance
        ChannelMember channelMember = new ChannelMember();

        // Find the member by memberId
        WorkspaceMember member = workspaceMemberRepository.findById(workspaceMemberDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + creatorId));

        channelMember.setMember(member);
        channelMember.setChannel(channel);
        channelMemberRepository.save(channelMember);
    }

    public void removeChannelMember(User user,
                                    Long creatorId,
                                    Long channelId,
                                    WorkspaceMemberDto workspaceMemberDto) throws ResourceAccessDeniedException {
        // Find the creator by creatorId
        WorkspaceMember creator = workspaceMemberRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + creatorId));

        // Check if the WorkspaceMember belongs to the provided user
        if (!creator.getUser().equals(user)) {
            throw new ResourceAccessDeniedException("User is unauthorized to access channels for workspace member with ID " + creatorId);
        }

        // Find the Channel by channelId
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));

        // Check if the member is the channel's creator
        if (!channel.getCreator().getId().equals(creatorId)) {
            throw new ResourceAccessDeniedException("User is unauthorized to update channel with ID " + channelId);
        }

        channelMemberRepository.deleteByMemberId(workspaceMemberDto.getId());
    }
}
