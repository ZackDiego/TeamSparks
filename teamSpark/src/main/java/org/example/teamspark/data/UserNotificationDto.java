package org.example.teamspark.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.data.dto.MessageDto;
import org.example.teamspark.data.dto.UserDto;
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
    private MessageDto messageDto;

    @JsonProperty("from_user")
    private UserDto fromUser;

    @JsonProperty("is_seen")
    private Boolean isSeen;
}