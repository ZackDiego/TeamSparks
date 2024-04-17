package org.example.teamspark.model.workspace;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.model.user.User;

import java.io.Serializable;

@Entity
@Table(name = "workspace_member")
@Data
@NoArgsConstructor
@IdClass(WorkspaceMember.WorkspaceMemberId.class)
public class WorkspaceMember implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NoArgsConstructor
    @Data
    public static class WorkspaceMemberId implements Serializable {

        private Long workspace;

        private Long user;

    }

}
