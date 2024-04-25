package org.example.teamspark.repository;

import org.example.teamspark.model.channel.Channel;
import org.example.teamspark.model.channel.ChannelMember;
import org.example.teamspark.model.workspace.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    List<ChannelMember> findByMemberId(Long memberId);

    List<ChannelMember> findByChannelId(Long channelId);

    List<ChannelMember> findByChannelIn(List<Channel> channels);

    List<ChannelMember> findByChannel(Channel channel);

    void deleteByMemberId(Long memberId);

    @Query("SELECT cm.member FROM ChannelMember cm WHERE cm.channel.id = :channelId AND cm.isCreator = true")
    WorkspaceMember findCreatorByChannelId(@Param("channelId") Long channelId);
}
