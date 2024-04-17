package org.example.teamspark.model.role;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.model.user.User;

import java.io.Serializable;

@Entity
@Table(name = "user_role")
@Data
@NoArgsConstructor
@IdClass(UserRole.UserRoleId.class)
public class UserRole implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "group_by_role")
    private Boolean groupByRole;

    @NoArgsConstructor
    @Data
    public static class UserRoleId implements Serializable {
        private Long role;
        private Long user;
    }
}