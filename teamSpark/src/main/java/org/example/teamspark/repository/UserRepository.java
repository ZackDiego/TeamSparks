package org.example.teamspark.repository;

import org.example.teamspark.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    @Query(value = "SELECT " +
            "    u.id " +
            "FROM " +
            "    user u " +
            "    INNER JOIN workspace_member wm ON u.id = wm.user_id " +
            "    INNER JOIN channel_member cm ON wm.id = cm.member_id " +
            "WHERE " +
            "    cm.channel_id = :channelId",
            nativeQuery = true)
    List<Long> getUserIdsByChannelId(@Param("channelId") Long channelId);
}
