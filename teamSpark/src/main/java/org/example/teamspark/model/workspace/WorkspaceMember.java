package org.example.teamspark.model.workspace;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.model.user.User;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "workspace_member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMember implements Serializable {

    @EmbeddedId
    private WorkspaceMemberId id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @MapsId("workspaceId")
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at")
    private Date joinedAt = new Date();

    public static WorkspaceMember create(Workspace workspace, User user) {
        WorkspaceMember.WorkspaceMemberId workspaceMemberId = new WorkspaceMember.WorkspaceMemberId();
        workspaceMemberId.setWorkspaceId(workspace.getId());
        workspaceMemberId.setUserId(user.getId());

        WorkspaceMember newMember = new WorkspaceMember();
        newMember.setId(workspaceMemberId);
        newMember.setWorkspace(workspace);
        newMember.setUser(user);

        return newMember;
    }

    @NoArgsConstructor
    @Data
    @Embeddable
    public static class WorkspaceMemberId implements Serializable {

        @Column(name = "workspace_id")
        private Long workspaceId;

        @Column(name = "user_id")
        private Long userId;
    }

}
