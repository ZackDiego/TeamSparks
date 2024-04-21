package org.example.teamspark.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDocument {
    private String messageId;
    private Long fromId;
    private String fromName;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private Boolean containLink;
    private String fileUrl;
    private String imageUrl;
}
