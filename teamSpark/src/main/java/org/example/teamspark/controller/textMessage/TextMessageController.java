package org.example.teamspark.controller.textMessage;

import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.data.dto.MessageDto;
import org.example.teamspark.data.dto.message.InMessageDto;
import org.example.teamspark.service.MessageHistoryService;
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
    private final ModelMapper modelMapper;

    @Autowired
    public TextMessageController(SimpMessagingTemplate messageTemplate, MessageHistoryService messageHistoryService, ModelMapper modelMapper) {
        this.messageTemplate = messageTemplate;
        this.messageHistoryService = messageHistoryService;

        this.modelMapper = modelMapper;
    }

    @MessageMapping("/textMessagingEndpoint")
    public void handleTextMessage(InMessageDto inMessageDto) {

        MessageDto messageDto = modelMapper.map(inMessageDto.getMessage(), MessageDto.class);

        // store message to elasticsearch
        messageHistoryService.addMessageHistoryByChannelId(inMessageDto.getChannelId(), messageDto);

        // Send to Message to channel
        messageTemplate.convertAndSend(
                "/textMessagingChannel/" + inMessageDto.getChannelId(),
                messageDto);
    }
}
