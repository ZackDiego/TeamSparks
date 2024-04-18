package org.example.teamspark.model.workspace;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.model.user.User;

import java.util.Date;

@Entity
@Table(name = "workspace")
@Data
@NoArgsConstructor
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "avatar")
    private String avatar;
}
