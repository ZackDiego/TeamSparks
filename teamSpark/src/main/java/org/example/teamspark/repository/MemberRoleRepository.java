package org.example.teamspark.repository;

import org.example.teamspark.model.workspace.role.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {
    MemberRole findByWorkspaceMemberIdAndRoleId(Long memberId, Long roleId);
}
