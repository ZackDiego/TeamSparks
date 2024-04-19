package org.example.teamspark.model.label;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.model.workspace.WorkspaceMember;

@Entity
@Table(name = "label")
@Data
@NoArgsConstructor
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private WorkspaceMember creator;
}
