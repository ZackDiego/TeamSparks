package org.example.teamspark.data.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageId {
    private Long channelId;
    private String messageObjectId;
}
