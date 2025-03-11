package org.example.teamspark.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.apachecommons.CommonsLog;
import org.bson.types.ObjectId;
import org.example.teamspark.data.dto.message.ChannelMessageHistoryDto;
import org.example.teamspark.data.dto.message.MessageDto;
import org.example.teamspark.data.dto.message.MessageId;
import org.example.teamspark.exception.ElasticsearchFailedException;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.channel.Channel;
import org.example.teamspark.model.channel.ChannelMember;
import org.example.teamspark.model.user.User;
import org.example.teamspark.repository.ChannelMemberRepository;
import org.example.teamspark.repository.ChannelRepository;
import org.example.teamspark.repository.MessageHistoryRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CommonsLog
public class MessageHistoryServiceV2 {
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;

    private final MessageHistoryRepository messageHistoryRepository;

    private final MongoTemplate mongoTemplate;

    public MessageHistoryServiceV2(MessageHistoryRepository messageHistoryRepository,
                                   ChannelRepository channelRepository,
                                   ChannelMemberRepository channelMemberRepository,
                                   MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.messageHistoryRepository = messageHistoryRepository;
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
    }

    public ChannelMessageHistoryDto getMessagesByChannelId(Long channelId, User user) throws ResourceAccessDeniedException, JsonProcessingException, ElasticsearchFailedException {

        // Check if user belongs to the channel
        if (!isUserMemberOfChannel(user.getId(), channelId)) {
            throw new ResourceAccessDeniedException("User Id " + user.getId() + " does not belong to the channel Id " + channelId);
        }

        // Validate if the channel exists
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with ID: " + channelId));

        ChannelMessageHistoryDto channelMessageHistoryDto = messageHistoryRepository.findOneByChannelId(channelId);

        if (channelMessageHistoryDto == null) {
            ChannelMessageHistoryDto dto = new ChannelMessageHistoryDto();
            dto.setChannelId(channelId);
            dto.setPrivate(channel.getIsPrivate());
            return dto;
        }
        channelMessageHistoryDto.setPrivate(channel.getIsPrivate());
        return channelMessageHistoryDto;
    }

    public MessageId addMessageToChannelMessageHistory(Long channelId, MessageDto message) {

        // Define query criteria to find the document by channelId
        Criteria criteria = Criteria.where("channelId").is(channelId);

        // Assign Id to new message
        MessageId messageId = new MessageId(channelId, new ObjectId().toString());
        message.setMessageId(messageId);

        // Define the update operation to add the new message to the messages list
        Update update = new Update().addToSet("messages", message);

        // Execute the update using MongoTemplate
        mongoTemplate.updateFirst(Query.query(criteria), update, ChannelMessageHistoryDto.class);

        return messageId;
    }


    public boolean isUserMemberOfChannel(Long userId, Long channelId) {
        // Retrieve channel members by Channel ID
        List<ChannelMember> channelMembers = channelMemberRepository.findByChannelId(channelId);

        // Check if the provided user ID matches any of the member IDs using stream
        return channelMembers.stream()
                .anyMatch(member -> member.getMember().getUser().getId().equals(userId));
    }

    public void createNewMessageHistory(Long channelId) {
        ChannelMessageHistoryDto channelMessageHistoryDto = new ChannelMessageHistoryDto();
        channelMessageHistoryDto.setChannelId(channelId);
        messageHistoryRepository.save(channelMessageHistoryDto);
    }

}
