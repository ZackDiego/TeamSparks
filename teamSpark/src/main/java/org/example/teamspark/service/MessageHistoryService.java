package org.example.teamspark.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.data.dto.MessageDto;
import org.example.teamspark.data.dto.MessageHistoryDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.channel.Channel;
import org.example.teamspark.model.channel.ChannelMember;
import org.example.teamspark.model.user.User;
import org.example.teamspark.repository.ChannelMemberRepository;
import org.example.teamspark.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@CommonsLog
public class MessageHistoryService {
    private final ElasticsearchService elasticsearchService;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;

    public MessageHistoryService(ElasticsearchService elasticsearchService,
                                 ChannelRepository channelRepository,
                                 ChannelMemberRepository channelMemberRepository) {
        this.elasticsearchService = elasticsearchService;
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
    }

    private List<MessageDto> mapResponseBodyToMessageDocuments(String responseBody) throws JsonProcessingException {
        // map response body to
        ObjectMapper objectMapper = new ObjectMapper();

        // Deserialize the Elasticsearch response body to a JsonNode
        JsonNode responseObj = objectMapper.readTree(responseBody);

        // Extract the hits from the responseBody
        JsonNode hits = responseObj.get("hits").get("hits");

        Stream<JsonNode> hitsStream = StreamSupport.stream(hits.spliterator(), false);

        // Map each hit to your model class using ObjectMapper and collect them into a List
        return hitsStream
                .map(hit -> {
                    JsonNode source = hit.get("_source");
                    MessageDto dto = new MessageDto();
                    try {
                        dto = objectMapper.treeToValue(hit.get("_source"), MessageDto.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    dto.setMessageId(hit.get("_id").asText());
                    return dto;
                })
                .toList();
    }

    public MessageHistoryDto getMessagesByChannelId(Long channelId, User user) throws ResourceAccessDeniedException, JsonProcessingException {

        if (!isUserMemberOfChannel(user.getId(), channelId)) {
            throw new ResourceAccessDeniedException("User Id " + user.getId() + " does not belong to the channel Id " + channelId);
        }

        // Find the Channel by channelId
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));

        String indexName = "channel-" + channelId;
        // Find the message history in elasticsearch
        String responseBody = elasticsearchService.getDocumentsByIndexName(indexName);

        List<MessageDto> messageDtos = mapResponseBodyToMessageDocuments(responseBody);

        MessageHistoryDto dto = new MessageHistoryDto();
        dto.setChannelId(channelId);
        dto.setIsPrivate(channel.getIsPrivate());

        dto.setMessages(messageDtos);

        return dto;
    }

    public void createMessageHistoryByChannelId(User user, Long channelId) {

    }

    public void addMessageHistoryByChannelId(Long channelId, MessageDto message) {

        ObjectMapper objectMapper = new ObjectMapper();
        String messageJson = null;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            // Handle JSON serialization exception
            log.error("Error when converting message to json");
        }
        String indexName = "channel-" + channelId;
        elasticsearchService.addDocumentToIndex(indexName, messageJson);
    }


    public boolean isUserMemberOfChannel(Long userId, Long channelId) {
        // Retrieve channel members by Channel ID
        List<ChannelMember> channelMembers = channelMemberRepository.findByChannelId(channelId);

        // Check if the provided user ID matches any of the member IDs using stream
        return channelMembers.stream()
                .anyMatch(member -> member.getMember().getUser().getId().equals(userId));
    }
}
