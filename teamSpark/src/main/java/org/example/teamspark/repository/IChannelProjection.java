package org.example.teamspark.repository;

import java.util.Date;

public interface IChannelProjection {
    Long getChannelId();

    Long getWorkspaceId();

    String getChannelName();

    Date getCreatedAt();

    boolean getIsPrivate();

    Long getMemberId();

    Long getMemberUserId();

    String getMemberName();

    String getMemberAvatar();

    boolean getIsCreator();
}
