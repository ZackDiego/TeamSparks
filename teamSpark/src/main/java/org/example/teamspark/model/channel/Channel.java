package org.example.teamspark.model.channel;

import jakarta.persistence.*;
import lombok.Data;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.Workspace;

import java.util.Date;

@Entity
@Table(name = "channel")
@Data
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "is_private")
    private Boolean isPrivate;
}
