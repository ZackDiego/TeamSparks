package org.example.teamspark.controller.textMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.data.dto.message.InMessageDto;
import org.example.teamspark.data.dto.message.MessageDto;
import org.example.teamspark.data.dto.message.MessageId;
import org.example.teamspark.exception.ElasticsearchFailedException;
import org.example.teamspark.redis.RedisMessagePublisher;
import org.example.teamspark.service.MessageHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@CommonsLog
public class TextMessageController {
    private final MessageHistoryService messageHistoryService;
    private final RedisMessagePublisher redisMessagePublisher;

    @Autowired
    public TextMessageController(MessageHistoryService messageHistoryService,
                                 RedisMessagePublisher redisMessagePublisher) {
        this.messageHistoryService = messageHistoryService;
        this.redisMessagePublisher = redisMessagePublisher;
    }

    @MessageMapping("/textMessagingEndpoint")
    public void handleTextMessage(InMessageDto inMessageDto) throws JsonProcessingException, ElasticsearchFailedException {

        MessageDto messageDto = inMessageDto.getMessage();

        // store message to elasticsearch
        MessageId messageId = messageHistoryService.addMessageHistoryByChannelId(inMessageDto.getChannelId(), messageDto);
        messageDto.setMessageId(messageId);
        // save message id
        inMessageDto.setMessage(messageDto);
        // Send the message to message broker
        redisMessagePublisher.publish(inMessageDto);
    }
}
