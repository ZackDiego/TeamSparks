package org.example.teamspark.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "type")
    private NotificationType type;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "is_seen")
    private boolean isSeen = false;
}
