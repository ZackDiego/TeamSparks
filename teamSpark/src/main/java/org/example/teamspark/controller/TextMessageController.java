package org.example.teamspark.controller;

import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.model.InGroupMessage;
import org.example.teamspark.model.InMessage;
import org.example.teamspark.model.OutMessage;
import org.example.teamspark.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@CommonsLog
public class TextMessageController {

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @Autowired
    private ElasticsearchService elasticsearchService;

    private static String concatenateAndSort(String str1, String str2) {
        String result = str1.compareTo(str2) < 0 ? str1 + "_" + str2 : str2 + "_" + str1;
        return result.toLowerCase();
    }

    @MessageMapping("/groupTextChat")
    public void handleGroupMessage(InGroupMessage message) throws Exception {

        OutMessage outMessage = new OutMessage(message.getFrom() + " send: " + message.getContent());
        outMessage.setFrom(message.getFrom());
        // store message to elasticsearch
        elasticsearchService.addMessageToIndex(message.getChatGroup().toLowerCase(), outMessage);

        messageTemplate.convertAndSend(
                "/textChatGroup/" + message.getChatGroup(),
                outMessage);
    }

    @MessageMapping("/privateTextChat")
    public void handlePrivateMessage(InMessage message) {

        OutMessage outMessage = new OutMessage();
        outMessage.setFrom(message.getFrom());
        outMessage.setContent(message.getFrom() + " send: " + message.getContent());

        // store message to elasticsearch
        elasticsearchService.addMessageToIndex(
                concatenateAndSort(message.getFrom(), message.getTo()),
                outMessage);

        // Send to Message Receiver
        messageTemplate.convertAndSend(
                "/privateTextChat/" + message.getTo(),
                outMessage);

        // Send to Message Sender
        messageTemplate.convertAndSend(
                "/privateTextChat/" + message.getFrom(),
                outMessage);
    }
}
