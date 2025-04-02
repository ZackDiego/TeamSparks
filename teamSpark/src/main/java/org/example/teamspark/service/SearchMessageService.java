package org.example.teamspark.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.teamspark.data.dto.SearchCondition;
import org.example.teamspark.data.dto.message.MessageDto;
import org.example.teamspark.exception.ElasticsearchFailedException;
import org.example.teamspark.model.channel.Channel;
import org.example.teamspark.model.user.User;
import org.example.teamspark.repository.ChannelRepository;
import org.example.teamspark.repository.MessageHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchMessageService {
    private final MessageHistoryRepository messageHistoryRepository;
    private final ChannelRepository channelRepository;

    public SearchMessageService(MessageHistoryRepository messageHistoryRepository, ChannelRepository channelRepository) {
        this.messageHistoryRepository = messageHistoryRepository;
        this.channelRepository = channelRepository;
    }

    public List<MessageDto> getSearchMessages(User user, SearchCondition searchCondition) throws ElasticsearchFailedException, JsonProcessingException {

        // Step 1: Get the channels the user has access to
        List<Channel> userChannels = channelRepository.findChannelsByUserId(user.getId());
        List<Long> accessibleChannelIds = userChannels.stream()
                .map(Channel::getId)
                .collect(Collectors.toList());

        if (accessibleChannelIds.isEmpty()) {
            return Collections.emptyList(); // No accessible channels, return empty result
        }

        // Step 2: Validate specific channelId if provided
        if (searchCondition.getChannelId() != null) {
            boolean hasAccess = accessibleChannelIds.contains(searchCondition.getChannelId());
            if (!hasAccess) {
                throw new ResourceAccessException("User doesn't have access to channel id " +
                        searchCondition.getChannelId());
            }
            // Narrow to just search condition specify channel
            accessibleChannelIds.clear();
            accessibleChannelIds.add(searchCondition.getChannelId());
        }

        searchCondition.setAccessibleChannelIds(accessibleChannelIds);

        return messageHistoryRepository.findMessagesWithSearchCondition(searchCondition);
    }
}
