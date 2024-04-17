package org.example.teamspark.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class ChatMessage {
    private String indexName;
    private Long messageId;
    private Long workspaceId;
    private Long channelId;
    private Long fromUserId;
    private String fromUserName;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private Boolean containLink;
    private String fileUrl;
    private String imageUrl;

    public ChatMessage() {
        this.indexName = "channel_" + this.channelId;
    }
}
