package org.example.teamspark.repository;

import org.example.teamspark.model.workspace.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    @Query(value = "SELECT " +
            "w.id, " +
            "w.name, " +
            "w.created_at, " +
            "w.avatar, " +
            "wm.id AS memberId, " +
            "u.id AS userId, " +
            "u.name AS userName, " +
            "u.avatar AS userAvatar, " +
            "wm.is_creator AS isCreator " +
            "FROM user u " +
            "LEFT JOIN workspace_member wm ON u.id = wm.user_id " +
            "LEFT JOIN workspace w ON wm.workspace_id = w.id " +
            "WHERE wm.user_id = :userId",
            nativeQuery = true)
    List<Object[]> findWorkspaceWithMembersByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT " +
            "w.id, " +
            "w.name, " +
            "w.created_at, " +
            "w.avatar, " +
            "wm.id, " +
            "u.id, " +
            "u.name, " +
            "u.avatar, " +
            "wm.is_creator " +
            "FROM workspace w " +
            "LEFT JOIN workspace_member wm ON w.id = wm.workspace_id " +
            "LEFT JOIN user u ON wm.user_id = u.id " +
            "WHERE w.id = :id",
            nativeQuery = true)
    List<Object[]> findWorkspaceWithMembersById(@Param("id") Long id);
}
