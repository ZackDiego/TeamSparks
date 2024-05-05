package org.example.teamspark.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.data.dto.message.MessageDto;
import org.example.teamspark.model.user.NotificationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("user")
    private UserDto user;

    @JsonProperty("type")
    private NotificationType type;

    @JsonProperty("message")
    private MessageDto message;

    @JsonProperty("is_seen")
    private Boolean isSeen;
}
