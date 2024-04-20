package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.teamspark.data.dto.ChannelDto;
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

import java.util.*;

@Service
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public ChannelService(ChannelRepository channelRepository,
                          ChannelMemberRepository channelMemberRepository,
                          WorkspaceMemberRepository workspaceMemberRepository) {
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    private static List<ChannelDto> mapResultSetToChannelDtos(List<Object[]> rs) {
        // Map the result rows to DTO objects
        Map<Long, ChannelDto> channelDtoMap = new HashMap<>();
        for (Object[] row : rs) {
            Long channelId = (Long) row[0];

            // Create or retrieve the ChannelDto from the map
            ChannelDto channelDto = channelDtoMap.computeIfAbsent(channelId, id -> {
                ChannelDto newChannelDto = new ChannelDto();
                newChannelDto.setId(channelId);
                newChannelDto.setName((String) row[1]);
                newChannelDto.setPrivate((Boolean) row[2]);

                // creator
                WorkspaceMemberDto creatorDto = new WorkspaceMemberDto();
                creatorDto.setId((Long) row[3]);
                creatorDto.setName((String) row[4]);
                creatorDto.setAvatar((String) row[5]);
                newChannelDto.setCreator(creatorDto);

                newChannelDto.setCreatedAt((Date) row[9]);

                newChannelDto.setMembers(new ArrayList<>());
                return newChannelDto;
            });

            // Create a WorkspaceMemberDto for the member
            WorkspaceMemberDto memberDto = new WorkspaceMemberDto();
            memberDto.setId((Long) row[6]);
            memberDto.setName((String) row[7]);
            memberDto.setAvatar((String) row[8]);

            // Add the member to the channelDto
            channelDto.getMembers().add(memberDto);

            channelDtoMap.put(channelId, channelDto);
        }

        List<ChannelDto> channelDtos = new ArrayList<>(channelDtoMap.values());
        return channelDtos;
    }

    public ChannelDto createChannel(User user, Long creatorId, ChannelDto channelDto) throws ResourceAccessDeniedException {
        // Find the WorkspaceMember by memberId
        WorkspaceMember workspaceMember = workspaceMemberRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + creatorId));

        // Check if the WorkspaceMember belongs to the provided user
        if (!workspaceMember.getUser().equals(user)) {
            throw new ResourceAccessDeniedException("User is unauthorized to access channels for workspace member with ID " + creatorId);
        }

        // Create a new Channel instance
        Channel channel = new Channel();
        channel.setWorkspace(workspaceMember.getWorkspace());
        channel.setName(channelDto.getName());
        channel.setCreator(workspaceMember);
        channel.setIsPrivate(channelDto.isPrivate());

        Channel createdChannel = channelRepository.save(channel);

        // add the creator as channel member
        ChannelMember newMember = new ChannelMember();
        newMember.setMember(workspaceMember);
        newMember.setChannel(createdChannel);
        ChannelMember savedMember = channelMemberRepository.save(newMember);

        // form the savedChannelDto
        List<Object[]> rs = channelRepository.findChannelsWithMembersByChannelId(createdChannel.getId());

        return mapResultSetToChannelDtos(rs).get(0);
    }

    public List<ChannelDto> getChannelsByMemberId(User user, Long memberId) throws ResourceAccessDeniedException {

        // Find the WorkspaceMember by memberId
        WorkspaceMember workspaceMember = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + memberId));

        // Check if the WorkspaceMember belongs to the provided user
        if (!workspaceMember.getUser().equals(user)) {
            throw new ResourceAccessDeniedException("User is unauthorized to access channels for workspace member with ID " + memberId);
        }

        List<Object[]> rs = channelRepository.findChannelsWithMembersByMemberId(memberId);

        return mapResultSetToChannelDtos(rs);
    }

    public ChannelDto updateChannel(User user, Long creatorId, Long channelId, ChannelDto channelDto) throws ResourceAccessDeniedException {

        // Find the WorkspaceMember by memberId
        WorkspaceMember workspaceMember = workspaceMemberRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + creatorId));

        // Check if the WorkspaceMember belongs to the provided user
        if (!workspaceMember.getUser().equals(user)) {
            throw new ResourceAccessDeniedException("User is unauthorized to access channels for workspace member with ID " + creatorId);
        }

        // Find the Channel by channelId
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));

        // Check if the member is the channel's creator
        if (!channel.getCreator().getId().equals(creatorId)) {
            throw new ResourceAccessDeniedException("User is unauthorized to update channel with ID " + channelId);
        }

        // Update the channel details
        channel.setName(channelDto.getName());
        channel.setIsPrivate(channelDto.isPrivate());

        // Save the updated channel
        channelRepository.save(channel);

        // form the updatedChannelDto
        List<Object[]> rs = channelRepository.findChannelsWithMembersByChannelId(channelId);

        return mapResultSetToChannelDtos(rs).get(0);
    }

    public void deleteChannel(User user, Long creatorId, Long channelId) throws ResourceAccessDeniedException {

        // Find the WorkspaceMember by memberId
        WorkspaceMember workspaceMember = workspaceMemberRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + creatorId));

        // Check if the WorkspaceMember belongs to the provided user
        if (!workspaceMember.getUser().equals(user)) {
            throw new ResourceAccessDeniedException("User is unauthorized to access channels for workspace member with ID " + creatorId);
        }

        // Find the Channel by channelId
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));

        // Check if the member is the channel's creator
        if (!channel.getCreator().getId().equals(creatorId)) {
            throw new ResourceAccessDeniedException("User is unauthorized to delete channel with ID " + channelId);
        }

        channelRepository.delete(channel);
    }
}
