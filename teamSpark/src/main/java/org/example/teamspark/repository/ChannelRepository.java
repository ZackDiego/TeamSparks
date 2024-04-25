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

    @Query("SELECT cm.channel FROM ChannelMember cm WHERE cm.member.id = :memberId")
    List<Channel> findChannelsByMemberId(@Param("memberId") Long memberId);

    @Query(value = "SELECT " +
            "    c.id AS channelId, " +
            "    c.name AS channelName, " +
            "    c.created_at AS createdAt " +
            "    c.is_private AS channelIsPrivate, " +
            "    wm.id AS memberId, " +
            "    u.id AS memberUserId, " +
            "    u.name AS memberName, " +
            "    u.avatar AS memberAvatar, " +
            "FROM " +
            "    channel c " +
            "    LEFT JOIN channel_member cm ON c.id = cm.channel_id " +
            "    LEFT JOIN workspace_member wm ON cm.member_id = wm.id " +
            "    LEFT JOIN user u ON wm.user_id = u.id " +
            "WHERE " +
            "    c.id = :channelId",
            nativeQuery = true)
    List<Object[]> findChannelWithMembersByChannelId(@Param("channelId") Long channelId);


    @Query(value = "SELECT " +
            "    c.id AS channelId, " +
            "    c.workspace_id AS workSpaceId, " +
            "    c.name AS channelName, " +
            "    c.created_at AS createdAt, " +
            "    c.is_private AS channelIsPrivate, " +
            "    wm.id AS memberId, " +
            "    u.id AS userId, " +
            "    u.name AS userName, " +
            "    u.avatar AS userAvatar, " +
            "    wm.is_creator AS memberIsCreator " +
            "FROM " +
            "    channel c " +
            "    LEFT JOIN channel_member cm ON c.id = cm.channel_id " +
            "    LEFT JOIN workspace_member wm ON cm.member_id = wm.id " +
            "    LEFT JOIN user u ON wm.user_id = u.id " +
            "WHERE " +
            "    c.id IN :channelIds",
            nativeQuery = true)
    List<Object[]> findChannelsWithMembersByChannelIds(@Param("channelIds") List<Long> channelIds);


    @Query(value = "SELECT c.* FROM channel c " +
            "INNER JOIN channel_member cm ON c.id = cm.channel_id " +
            "INNER JOIN workspace_member wm ON cm.member_id = wm.id " +
            "WHERE wm.user_id = :userId", nativeQuery = true)
    List<Channel> findChannelsByUserId(@Param("userId") Long userId);
}
