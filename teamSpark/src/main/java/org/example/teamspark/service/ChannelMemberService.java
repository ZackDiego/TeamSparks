package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.channel.Channel;
import org.example.teamspark.model.channel.ChannelMember;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.WorkspaceMember;
import org.example.teamspark.repository.ChannelMemberRepository;
import org.example.teamspark.repository.ChannelRepository;
import org.example.teamspark.repository.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                                 Long channelId,
                                 Long wsMemberId) throws ResourceAccessDeniedException {

        // Find the Channel by channelId
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));

        // Check if user belongs to the channel
        Boolean isChannelMember = channelMemberRepository.checkUserChannelMember(user.getId(), channelId);

        if (!isChannelMember) {
            throw new ResourceAccessDeniedException("User is unauthorized to access channel " + channelId);
        }

        // Create new channelMember instance
        ChannelMember channelMember = new ChannelMember();

        // Find the member by memberId
        WorkspaceMember member = workspaceMemberRepository.findById(wsMemberId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + wsMemberId));

        channelMember.setMember(member);
        channelMember.setChannel(channel);
        channelMemberRepository.save(channelMember);
    }

    @Transactional
    public void removeChannelMember(User user,
                                    Long channelId,
                                    Long memberId) throws ResourceAccessDeniedException {

        WorkspaceMember creator = channelMemberRepository.findCreatorByChannelId(channelId);

        // Check if the member is the channel's creator
        if (!creator.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        channelMemberRepository.deleteByMemberId(memberId);
    }
}
