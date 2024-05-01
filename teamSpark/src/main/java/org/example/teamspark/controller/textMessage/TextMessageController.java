package org.example.teamspark.controller.textMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.data.dto.message.InMessageDto;
import org.example.teamspark.data.dto.message.MessageDto;
import org.example.teamspark.data.dto.message.MessageId;
import org.example.teamspark.exception.ElasticsearchFailedException;
import org.example.teamspark.service.MessageHistoryService;
import org.example.teamspark.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@CommonsLog
public class TextMessageController {

    private final SimpMessagingTemplate messageTemplate;
    private final MessageHistoryService messageHistoryService;
    private final NotificationService notificationService;

    @Autowired
    public TextMessageController(SimpMessagingTemplate messageTemplate,
                                 MessageHistoryService messageHistoryService,
                                 NotificationService notificationService) {
        this.messageTemplate = messageTemplate;
        this.messageHistoryService = messageHistoryService;
        this.notificationService = notificationService;
    }

    @MessageMapping("/textMessagingEndpoint")
    public void handleTextMessage(InMessageDto inMessageDto) throws JsonProcessingException, ElasticsearchFailedException {

        MessageDto messageDto = inMessageDto.getMessage();

        // store message to elasticsearch
        MessageId messageId = messageHistoryService.addMessageHistoryByChannelId(inMessageDto.getChannelId(), messageDto);
        messageDto.setMessageId(messageId);
        notificationService.handleChannelMessageNotifications(inMessageDto.getChannelId(), messageDto);

        // Send to Message to channel
        messageTemplate.convertAndSend(
                "/textMessagingChannel/" + inMessageDto.getChannelId(),
                messageDto);
    }
}
