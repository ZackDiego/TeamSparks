package org.example.teamspark.data.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "channel_message_histories")
public class ChannelMessageHistoryDto {

    @Id
    private String id;

    @JsonProperty("channel_id")
    @Indexed(unique = true)
    private Long channelId;

    @JsonProperty("messages")
    private List<MessageDto> messages;

}
