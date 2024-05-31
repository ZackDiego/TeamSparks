package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.data.dto.UserDto;
import org.example.teamspark.data.dto.UserNotificationDto;
import org.example.teamspark.data.dto.message.MessageDto;
import org.example.teamspark.data.dto.message.MessageId;
import org.example.teamspark.model.user.NotificationType;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.user.UserNotification;
import org.example.teamspark.repository.UserNotificationRepository;
import org.example.teamspark.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CommonsLog
public class NotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messageTemplate;

    public NotificationService(UserNotificationRepository userNotificationRepository,
                               UserRepository userRepository,
                               SimpMessagingTemplate messageTemplate) {
        this.userNotificationRepository = userNotificationRepository;
        this.userRepository = userRepository;
        this.messageTemplate = messageTemplate;
    }

    public void handleChannelMessageNotifications(Long channelId, MessageDto messageDto) {

        List<Long> userIds = userRepository.getUserIdsByChannelId(channelId);

        // Save notifications in DB
        List<UserNotification> notifications = userIds.stream().map(userId -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

            UserNotification notification = new UserNotification();

            notification.setUser(user);
            notification.setType(NotificationType.MESSAGE);

            MessageId messageId = messageDto.getMessageId();
            notification.setMessageIndexName(messageId.getIndexName());
            notification.setMessageDocumentId(messageId.getDocumentId());

            // TODO: save when user offline
//            return userNotificationRepository.save(notification);
            return notification;
        }).toList();

        // Transform userNotification to userNotificationDto
        List<UserNotificationDto> dtos = notifications.stream().map(notification -> {
            UserNotificationDto dto = new UserNotificationDto();
            dto.setId(notification.getId());
            dto.setUser(UserDto.from(notification.getUser()));
            dto.setType(notification.getType());
            dto.setMessage(messageDto);
            dto.setIsSeen(notification.isSeen());
            return dto;
        }).toList();

        // Send dto to users
        notifyUsers(dtos);
    }

    public void notifyUsers(List<UserNotificationDto> dtos) {
        dtos.forEach(userNotification ->
                {
                    messageTemplate.convertAndSend("/userNotification/" + userNotification.getUser().getId()
                            , userNotification);
                    log.info("send notification to " + "/userNotification/" + userNotification.getUser().getId());
                }
        );
    }
}
