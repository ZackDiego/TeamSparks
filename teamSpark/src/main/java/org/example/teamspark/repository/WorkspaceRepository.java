package org.example.teamspark.repository;

import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByCreator(User creator);
}
