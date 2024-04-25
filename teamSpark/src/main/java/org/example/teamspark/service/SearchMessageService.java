package org.example.teamspark.service;

import org.example.teamspark.data.dto.MessageDto;
import org.example.teamspark.data.dto.SearchCondition;
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

    public List<MessageDto> getSearchMessages(User user, SearchCondition searchCondition) {

        // get the channels of user
        List<Channel> userChannels = channelRepository.findChannelsByUserId(user.getId());

        boolean hasAccess = userChannels.stream()
                .anyMatch(channel -> channel.getId().equals(searchCondition.getChannelId()));

        if (!hasAccess) {
            throw new ResourceAccessException("User don't have access to channel id " + searchCondition.getChannelId());
        }

//        List<MessageDto> messages = elasticsearchService.searchMessageWithCondition(userChannels, searchCondition);


//        List<MessageDto> messages =
        return null;
    }
}
