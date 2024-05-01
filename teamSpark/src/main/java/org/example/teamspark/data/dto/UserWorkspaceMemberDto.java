package org.example.teamspark.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWorkspaceMemberDto {
    @JsonProperty("workspace_id")
    private Long workspaceId;

    @JsonProperty("workspace_name")
    private String workspaceName;

    @JsonProperty("workspace_avatar")
    private String workspaceAvatar;

    @JsonProperty("member_id")
    private Long memberId;

    @JsonProperty("joined_at")
    private Date joinedAt;
}
