package org.example.teamspark.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("creator")
    private WorkspaceMemberDto creator;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("is_private")
    private boolean isPrivate;

    @JsonProperty("members")
    private List<WorkspaceMemberDto> members;
}
