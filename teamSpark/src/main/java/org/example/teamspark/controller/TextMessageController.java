package org.example.teamspark.controller;

import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.data.dto.message.InMessage;
import org.example.teamspark.data.dto.message.OutMessage;
import org.example.teamspark.service.ElasticsearchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@CommonsLog
public class TextMessageController {

    private final SimpMessagingTemplate messageTemplate;
    private final ElasticsearchService elasticsearchService;
    private final ModelMapper modelMapper;

    @Autowired
    public TextMessageController(SimpMessagingTemplate messageTemplate, ElasticsearchService elasticsearchService, ModelMapper modelMapper) {
        this.messageTemplate = messageTemplate;
        this.elasticsearchService = elasticsearchService;
        this.modelMapper = modelMapper;
    }

    @MessageMapping("/textMessagingEndpoint")
    public void handleTextMessage(InMessage message) {

        OutMessage outMessage = modelMapper.map(message, OutMessage.class);

        // store message to elasticsearch
        elasticsearchService.addMessageToIndex(message);

        // Send to Message to channel
        messageTemplate.convertAndSend(
                "/textMessagingChannel/" + message.getChannelId(),
                outMessage);
    }
}
