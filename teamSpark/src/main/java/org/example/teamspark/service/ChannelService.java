package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.teamspark.data.dto.ChannelDto;
import org.example.teamspark.data.dto.UserDto;
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
import java.util.stream.Collectors;

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

                UserDto userDto = new UserDto();
                // TODO: set id in userDto
                userDto.setName((String) row[4]);
                userDto.setAvatar((String) row[5]);

                creatorDto.setUserDto(userDto);
                
                newChannelDto.setCreatedAt((Date) row[9]);

                newChannelDto.setMembers(new ArrayList<>());
                return newChannelDto;
            });

            // Create a WorkspaceMemberDto for the member
            WorkspaceMemberDto memberDto = new WorkspaceMemberDto();
            memberDto.setId((Long) row[6]);

            UserDto userDto = new UserDto();
            // TODO: set id in userDto
            userDto.setName((String) row[7]);
            userDto.setAvatar((String) row[8]);

            memberDto.setUserDto(userDto);

            // Add the member to the channelDto
            channelDto.getMembers().add(memberDto);

            channelDtoMap.put(channelId, channelDto);
        }

        List<ChannelDto> channelDtos = new ArrayList<>(channelDtoMap.values());
        return channelDtos;
    }

    public ChannelDto createChannel(User user, ChannelDto channelDto) throws ResourceAccessDeniedException {
        // check user
        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceIdAndUserId(channelDto.getWorkspaceId(), user.getId());

        // Create a new Channel instance
        Channel channel = new Channel();
        channel.setWorkspace(member.getWorkspace());
        channel.setName(channelDto.getName());
        channel.setIsPrivate(channelDto.isPrivate());

        Channel createdChannel = channelRepository.save(channel);

        // add the creator as channel member
        ChannelMember newMember = new ChannelMember();
        newMember.setMember(member);
        newMember.setChannel(createdChannel);
        newMember.setCreator(true);
        ChannelMember savedMember = channelMemberRepository.save(newMember);

        // form the savedChannelDto
        List<Object[]> rs = channelRepository.findChannelWithMembersByChannelId(createdChannel.getId());

        return mapResultSetToChannelDtos(rs).get(0);
    }

    public List<ChannelDto> getChannelsByMemberId(User user, Long memberId) throws ResourceAccessDeniedException {

        // Find the member by memberId
        WorkspaceMember member = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace member not found with ID: " + memberId));

        // Check if the WorkspaceMember belongs to the provided user
        if (!member.getUser().equals(user)) {
            throw new ResourceAccessDeniedException("User is unauthorized to access channels for workspace member with ID " + memberId);
        }

//        List<Object[]> rs = channelRepository.findChannelsWithMembersByMemberId(memberId);

        List<Channel> channels = channelRepository.findChannelsByMemberId(memberId);
        List<Long> channelIds = channels.stream()
                .map(Channel::getId)
                .collect(Collectors.toList());

        List<Object[]> rs = channelRepository.findChannelsWithMembersByChannelIds(channelIds);

        return mapResultSetToChannelDtos(rs);
    }

    public ChannelDto updateChannel(User user, Long channelId, ChannelDto channelDto) throws ResourceAccessDeniedException {

        WorkspaceMember creator = channelMemberRepository.findCreatorByChannelId(channelId);

        // Find the Channel by channelId
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));

        // Check if the member is the channel's creator
        if (!creator.getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        // Update the channel details
        channel.setName(channelDto.getName());
        channel.setIsPrivate(channelDto.isPrivate());

        // Save the updated channel
        channelRepository.save(channel);

        // form the updatedChannelDto
        List<Object[]> rs = channelRepository.findChannelWithMembersByChannelId(channelId);

        return mapResultSetToChannelDtos(rs).get(0);
    }

    public void deleteChannel(User user, Long channelId) throws ResourceAccessDeniedException {

        WorkspaceMember creator = channelMemberRepository.findCreatorByChannelId(channelId);

        // Find the Channel by channelId
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));

        // Check if the member is the channel's creator
        if (!creator.getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("User is unauthorized to modify the workspace");
        }

        channelRepository.delete(channel);
    }
}
