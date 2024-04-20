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
            "cw.id AS creatorId, " + // Include the creator ID
            "cu.id AS creatorUserId, " + // Include the creator user ID
            "cu.name AS creatorName, " + // Include the creator name
            "cu.avatar AS creatorAvatar, " + // Include the creator avatar
            "wm.id, " +
            "u.id, " +
            "u.name, " +
            "u.avatar " +
            "FROM workspace w " +
            "JOIN workspace_member wm ON w.id = wm.workspace_id " +
            "JOIN user u ON wm.user_id = u.id " +
            "JOIN workspace_member cw ON w.creator_id = cw.id " + // Join with the creator workspace member
            "JOIN user cu ON cw.user_id = cu.id " + // Join with the creator user
            "WHERE wm.user_id = :userId",
            nativeQuery = true)
    List<Object[]> findWorkspaceWithMembersByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT " +
            "w.id, " +
            "w.name, " +
            "w.created_at, " +
            "w.avatar, " +
            "cw.id AS creatorId, " + // Include the creator ID
            "cu.id AS creatorUserId, " + // Include the creator user ID
            "cu.name AS creatorName, " + // Include the creator name
            "cu.avatar AS creatorAvatar, " + // Include the creator avatar
            "wm.id, " +
            "u.id, " +
            "u.name, " +
            "u.avatar " +
            "FROM workspace w " +
            "JOIN workspace_member wm ON w.id = wm.workspace_id " +
            "JOIN user u ON wm.user_id = u.id " +
            "JOIN workspace_member cw ON w.creator_id = cw.id " + // Join with the creator workspace member
            "JOIN user cu ON cw.user_id = cu.id " + // Join with the creator user
            "WHERE w.id = :id",
            nativeQuery = true)
    List<Object[]> findWorkspaceWithMembersById(@Param("id") Long id);
}
