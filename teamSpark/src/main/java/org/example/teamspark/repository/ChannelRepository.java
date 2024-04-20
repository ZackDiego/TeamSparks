package org.example.teamspark.repository;

import org.example.teamspark.model.channel.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    @Query(value = "SELECT " +
            "    c.id AS channelId, " +
            "    c.name AS channelName, " +
            "    c.is_private AS channelIsPrivate, " +
            "    cu.id AS creatorId, " +
            "    cu.name AS creatorName, " +
            "    cu.avatar AS creatorAvatar, " +
            "    wm.id AS memberId, " +
            "    u.name AS memberName, " +
            "    u.avatar AS memberAvatar, " +
            "    c.created_at AS createdAt " +
            "FROM " +
            "    channel c " +
            "    INNER JOIN channel_member cm ON c.id = cm.channel_id " +
            "    INNER JOIN workspace_member wm ON cm.member_id = wm.id " +
            "    INNER JOIN user u ON wm.user_id = u.id " +
            "    INNER JOIN workspace_member cw ON c.creator_id = cw.id " +
            "    INNER JOIN user cu ON cw.user_id = cu.id " +
            "WHERE " +
            "    wm.id = :memberId",
            nativeQuery = true)
    List<Object[]> findChannelsWithMembersByMemberId(@Param("memberId") Long memberId);

    @Query(value = "SELECT " +
            "    c.id AS channelId, " +
            "    c.name AS channelName, " +
            "    c.is_private AS channelIsPrivate, " +
            "    cu.id AS creatorId, " +
            "    cu.name AS creatorName, " +
            "    cu.avatar AS creatorAvatar, " +
            "    wm.id AS memberId, " +
            "    u.name AS memberName, " +
            "    u.avatar AS memberAvatar, " +
            "    c.created_at AS createdAt " +
            "FROM " +
            "    channel c " +
            "    INNER JOIN workspace_member wm ON c.creator_id = wm.id " +
            "    INNER JOIN user cu ON wm.user_id = cu.id " +
            "    INNER JOIN channel_member cm ON c.id = cm.channel_id " +
            "    INNER JOIN workspace_member wm2 ON cm.member_id = wm2.id " +
            "    INNER JOIN user u ON wm2.user_id = u.id " +
            "WHERE " +
            "    c.id = :channelId",
            nativeQuery = true)
    List<Object[]> findChannelsWithMembersByChannelId(@Param("channelId") Long channelId);
}
