package org.example.teamspark.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.data.dto.message.InMessageDto;
import org.example.teamspark.data.dto.message.MessageDto;
import org.example.teamspark.service.NotificationService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@CommonsLog
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messageTemplate;
    private final NotificationService notificationService;
    ObjectMapper objectMapper = new ObjectMapper();

    public RedisMessageSubscriber(SimpMessagingTemplate messageTemplate, NotificationService notificationService) {
        this.messageTemplate = messageTemplate;
        this.notificationService = notificationService;
    }

    public void onMessage(final Message message, final byte[] pattern) {
        try {
            InMessageDto inMessageDto = objectMapper.readValue(message.toString(), InMessageDto.class);

            MessageDto messageDto = inMessageDto.getMessage();

            // Send notification
            notificationService.handleChannelMessageNotifications(inMessageDto.getChannelId(), messageDto);

            // Send to Message to channel
            messageTemplate.convertAndSend(
                    "/textMessagingChannel/" + inMessageDto.getChannelId(),
                    messageDto);

        } catch (JsonProcessingException e) {
            log.error("Error when trying to convert message string to dto: " + e.getMessage());
        }
        log.info("Message received: " + new String(message.getBody()));
    }
}
