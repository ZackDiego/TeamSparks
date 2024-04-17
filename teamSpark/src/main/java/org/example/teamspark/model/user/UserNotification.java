package org.example.teamspark.model.user;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "type")
    private NotificationType type;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUserId;

    @Column(name = "content")
    private Date createdAt;

    @Column(name = "is_private")
    private Boolean isPrivate;
}
