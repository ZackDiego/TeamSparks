package org.example.teamspark.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.data.UserNotificationDto;
import org.example.teamspark.data.dto.MessageDto;
import org.example.teamspark.data.dto.UserDto;
import org.example.teamspark.model.user.NotificationType;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.user.UserNotification;
import org.example.teamspark.repository.UserNotificationRepository;
import org.example.teamspark.repository.UserRepository;
import org.example.teamspark.repository.WorkspaceMemberRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CommonsLog
public class NotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messageTemplate;

    public NotificationService(UserNotificationRepository userNotificationRepository,
                               WorkspaceMemberRepository workspaceMemberRepository,
                               UserRepository userRepository,
                               SimpMessagingTemplate messageTemplate) {
        this.userNotificationRepository = userNotificationRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
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
            notification.setChannelId(channelId);
            notification.setMessageId(messageDto.getMessageId());

            return userNotificationRepository.save(notification);
        }).toList();

        // Transform userNotification to userNotificationDto
        List<UserNotificationDto> dtos = notifications.stream().map(notification -> {
            UserNotificationDto dto = new UserNotificationDto();
            dto.setId(notification.getId());
            dto.setUser(UserDto.from(notification.getUser()));
            dto.setType(notification.getType());
            dto.setMessageDto(messageDto);

            User fromUser = workspaceMemberRepository.findUserByMemberId(messageDto.getFromId());
            dto.setFromUser(UserDto.from(fromUser));

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
