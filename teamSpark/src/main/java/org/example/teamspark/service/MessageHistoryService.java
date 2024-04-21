package org.example.teamspark.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import org.example.teamspark.data.dto.MessageHistoryDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.channel.Channel;
import org.example.teamspark.model.channel.ChannelMember;
import org.example.teamspark.model.message.MessageHistoryIndex;
import org.example.teamspark.model.user.User;
import org.example.teamspark.repository.ChannelMemberRepository;
import org.example.teamspark.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageHistoryService {
    private final ElasticsearchService elasticsearchService;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;

    public MessageHistoryService(ElasticsearchService elasticsearchService, ChannelRepository channelRepository, ChannelMemberRepository channelMemberRepository) {
        this.elasticsearchService = elasticsearchService;
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
    }

    public MessageHistoryDto getMessagesByChannelId(Long channelId, User user) throws ResourceAccessDeniedException, JsonProcessingException {

        if (!isUserMemberOfChannel(user.getId(), channelId)) {
            throw new ResourceAccessDeniedException("User Id " + user.getId() + " does not belong to the channel Id " + channelId);
        }

        // Find the Channel by channelId
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));

        // Find the message history in elasticsearch
        MessageHistoryIndex index = elasticsearchService.getMessageHistoryByIndexName("channel-" + channelId);


        return null;
    }

    public void createMessageHistoryByChannelId(User user, Long channelId) {

    }


    public boolean isUserMemberOfChannel(Long userId, Long channelId) {
        // Retrieve channel members by Channel ID
        List<ChannelMember> channelMembers = channelMemberRepository.findByChannelId(channelId);

        // Check if the provided user ID matches any of the member IDs using stream
        return channelMembers.stream()
                .anyMatch(member -> member.getMember().getUser().getId().equals(userId));
    }
}
