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
public class WorkspaceDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("creator")
    private UserDto creator;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("members")
    private List<UserDto> members;
}