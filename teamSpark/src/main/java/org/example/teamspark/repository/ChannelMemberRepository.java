package org.example.teamspark.repository;

import org.example.teamspark.model.channel.Channel;
import org.example.teamspark.model.channel.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    List<ChannelMember> findByMemberId(Long memberId);

    List<ChannelMember> findByChannelIn(List<Channel> channels);

    List<ChannelMember> findByChannel(Channel channel);

    void deleteByMemberId(Long memberId);
}
