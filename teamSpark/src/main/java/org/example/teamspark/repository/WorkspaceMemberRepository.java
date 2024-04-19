package org.example.teamspark.repository;

import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.Workspace;
import org.example.teamspark.model.workspace.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    void deleteByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    List<WorkspaceMember> findByWorkspace(Workspace workspace);

    void deleteByWorkspace(Workspace workspace);

    @Query("SELECT wm.user FROM WorkspaceMember wm WHERE wm.workspace = :workspace")
    Set<User> findUsersByWorkspace(@Param("workspace") Workspace workspace);

    @Query(value = "SELECT " +
            "    wm.id AS id, " +
            "    u.name AS name, " +
            "    u.avatar AS avatar, " +
            "    r.id AS roleId, " +
            "    r.name AS roleName, " +
            "    r.group_by_role AS groupByRole " +
            "FROM workspace_member wm " +
            "JOIN user u ON wm.user_id = u.id " +
            "LEFT JOIN member_role mr ON wm.id = mr.member_id " +
            "LEFT JOIN role r ON mr.role_id = r.id " +
            "WHERE wm.id = :workspaceMemberId",
            nativeQuery = true)
    List<Object[]> findWorkspaceMemberDtoById(@Param("workspaceMemberId") Long workspaceMemberId);
}
