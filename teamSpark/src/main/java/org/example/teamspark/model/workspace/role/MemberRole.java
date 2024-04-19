package org.example.teamspark.model.workspace.role;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.model.workspace.WorkspaceMember;

import java.io.Serializable;

@Entity
@Table(name = "member_role")
@Data
@NoArgsConstructor
@IdClass(MemberRole.MemberRoleId.class)
public class MemberRole implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Id
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private WorkspaceMember workspaceMember;

    @NoArgsConstructor
    @Data
    public static class MemberRoleId implements Serializable {
        private Long role;
        private Long workspaceMember;
    }
}