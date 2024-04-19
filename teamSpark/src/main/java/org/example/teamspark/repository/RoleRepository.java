package org.example.teamspark.repository;

import org.example.teamspark.model.workspace.Workspace;
import org.example.teamspark.model.workspace.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByWorkspace(Workspace workspace);
}
