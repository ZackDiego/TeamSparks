package org.example.teamspark.model.workspace.role;

import jakarta.persistence.*;
import lombok.Data;
import org.example.teamspark.model.workspace.Workspace;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @Column(name = "name")
    private String name;
}
