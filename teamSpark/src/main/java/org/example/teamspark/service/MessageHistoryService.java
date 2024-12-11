//package org.example.teamspark.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.extern.apachecommons.CommonsLog;
//import org.example.teamspark.data.dto.message.ChannelMessageHistoryDto;
//import org.example.teamspark.data.dto.message.MessageDto;
//import org.example.teamspark.data.dto.message.MessageId;
//import org.example.teamspark.exception.ElasticsearchFailedException;
//import org.example.teamspark.exception.ResourceAccessDeniedException;
//import org.example.teamspark.model.channel.Channel;
//import org.example.teamspark.model.channel.ChannelMember;
//import org.example.teamspark.model.user.User;
//import org.example.teamspark.repository.ChannelMemberRepository;
//import org.example.teamspark.repository.ChannelRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@CommonsLog
//public class MessageHistoryService {
//    private final ElasticsearchService elasticsearchService;
//    private final ChannelRepository channelRepository;
//    private final ChannelMemberRepository channelMemberRepository;
//
//    public MessageHistoryService(ElasticsearchService elasticsearchService,
//                                 ChannelRepository channelRepository,
//                                 ChannelMemberRepository channelMemberRepository) {
//        this.elasticsearchService = elasticsearchService;
//        this.channelRepository = channelRepository;
//        this.channelMemberRepository = channelMemberRepository;
//    }
//
//    public ChannelMessageHistoryDto getMessagesByChannelId(Long channelId, User user) throws ResourceAccessDeniedException, JsonProcessingException, ElasticsearchFailedException {
//
//        if (!isUserMemberOfChannel(user.getId(), channelId)) {
//            throw new ResourceAccessDeniedException("User Id " + user.getId() + " does not belong to the channel Id " + channelId);
//        }
//
//        // Find the Channel by channelId
//        Channel channel = channelRepository.findById(channelId)
//                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));
//
//        String indexName = "channel-" + channelId;
//        // Find the message history in elasticsearch
//        String responseBody = elasticsearchService.getDocumentsByIndexName(indexName);
//
//        List<MessageDto> messageDtos = elasticsearchService.mapSearchResultToMessageDocuments(responseBody);
//
//        ChannelMessageHistoryDto dto = new ChannelMessageHistoryDto();
//        dto.setChannelId(channelId);
//
//        dto.setMessages(messageDtos);
//
//        return dto;
//    }
//
//    public MessageId addMessageHistoryByChannelId(Long channelId, MessageDto message) throws JsonProcessingException, ElasticsearchFailedException {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String messageJson = null;
//        try {
//            messageJson = objectMapper.writeValueAsString(message);
//        } catch (Exception e) {
//            // Handle JSON serialization exception
//            log.error("Error when converting message to json");
//        }
//        String indexName = "channel-" + channelId;
//        return elasticsearchService.addDocumentToIndex(indexName, messageJson);
//    }
//
//
//    public boolean isUserMemberOfChannel(Long userId, Long channelId) {
//        // Retrieve channel members by Channel ID
//        List<ChannelMember> channelMembers = channelMemberRepository.findByChannelId(channelId);
//
//        // Check if the provided user ID matches any of the member IDs using stream
//        return channelMembers.stream()
//                .anyMatch(member -> member.getMember().getUser().getId().equals(userId));
//    }
//}
