package org.example.teamspark.data.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageHistoryDto {
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("is_private")
    private Boolean isPrivate;
    @JsonProperty("messages")
    private List<MessageDto> messages;
}
