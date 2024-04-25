package org.example.teamspark.model.channel;

import jakarta.persistence.*;
import lombok.Data;
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

    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "is_private")
    private Boolean isPrivate;
}
