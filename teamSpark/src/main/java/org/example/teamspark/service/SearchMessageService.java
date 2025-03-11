package org.example.teamspark.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.teamspark.data.dto.SearchCondition;
import org.example.teamspark.data.dto.message.MessageDto;
import org.example.teamspark.exception.ElasticsearchFailedException;
import org.example.teamspark.model.channel.Channel;
import org.example.teamspark.model.user.User;
import org.example.teamspark.repository.ChannelRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service
public class SearchMessageService {
    private final ElasticsearchService elasticsearchService;
    private final ChannelRepository channelRepository;


    public SearchMessageService(ElasticsearchService elasticsearchService, ChannelRepository channelRepository) {
        this.elasticsearchService = elasticsearchService;
        this.channelRepository = channelRepository;
    }

    public List<MessageDto> getSearchMessages(User user, SearchCondition searchCondition) throws ElasticsearchFailedException, JsonProcessingException {

        // get the channels of user
        List<Channel> userChannels = channelRepository.findChannelsByUserId(user.getId());

        if (searchCondition.getChannelId() != null) {
            boolean hasAccess = userChannels.stream()
                    .anyMatch(channel -> channel.getId().equals(searchCondition.getChannelId()));

            if (!hasAccess) {
                throw new ResourceAccessException("User don't have access to channel id " + searchCondition.getChannelId());
            }
        }

        String responseBody = elasticsearchService.searchMessageWithCondition(userChannels, searchCondition);

        return elasticsearchService.mapSearchResultToMessageDocuments(responseBody);
    }
}
