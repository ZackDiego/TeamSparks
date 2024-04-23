package org.example.teamspark.controller.textMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.data.dto.MessageDto;
import org.example.teamspark.data.dto.message.InMessageDto;
import org.example.teamspark.exception.ElasticsearchFailedException;
import org.example.teamspark.service.MessageHistoryService;
import org.example.teamspark.service.NotificationService;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @Autowired
    public TextMessageController(SimpMessagingTemplate messageTemplate, MessageHistoryService messageHistoryService, NotificationService notificationService, ModelMapper modelMapper) {
        this.messageTemplate = messageTemplate;
        this.messageHistoryService = messageHistoryService;
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    @MessageMapping("/textMessagingEndpoint")
    public void handleTextMessage(InMessageDto inMessageDto) throws JsonProcessingException, ElasticsearchFailedException {

        MessageDto messageDto = modelMapper.map(inMessageDto.getMessage(), MessageDto.class);

        // store message to elasticsearch
        String documentId = messageHistoryService.addMessageHistoryByChannelId(inMessageDto.getChannelId(), messageDto);
        messageDto.setMessageId(documentId);
        notificationService.handleChannelMessageNotifications(inMessageDto.getChannelId(), messageDto);

        // Send to Message to channel
        log.info("receive and send message to" + "/textMessagingChannel/" + inMessageDto.getChannelId() + "documentId" + documentId);
        messageTemplate.convertAndSend(
                "/textMessagingChannel/" + inMessageDto.getChannelId(),
                messageDto);
    }
}
