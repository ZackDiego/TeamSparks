package org.example.teamspark.controller;

import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.model.InGroupMessage;
import org.example.teamspark.model.InMessage;
import org.example.teamspark.model.OutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@CommonsLog
public class TextMessageController {

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @MessageMapping("/groupTextChat")
    public void handleGroupMessage(InGroupMessage message) throws Exception {
        messageTemplate.convertAndSend(
                "/textChatGroup/" + message.getChatGroup(),
                new OutMessage(message.getFrom() + " send: " + message.getContent()));
    }

    @MessageMapping("/privateTextChat")
    public void handlePrivateMessage(InMessage message) {
        // Send to Message Receiver
        messageTemplate.convertAndSend(
                "/privateTextChat/" + message.getTo(),
                new OutMessage(message.getFrom() + " send: " + message.getContent()));

        // Send to Message Sender
        messageTemplate.convertAndSend(
                "/privateTextChat/" + message.getFrom(),
                new OutMessage(message.getFrom() + " send: " + message.getContent()));
    }
}
