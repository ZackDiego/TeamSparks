package org.example.teamspark.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCondition {

    @JsonProperty("search_keyword")
    private String searchKeyword;

    @JsonProperty("from_id")
    private Long fromId;

    @JsonProperty("channel_id")
    private Long channelId;

    @JsonProperty("before_date")
    private Date beforeDate;

    @JsonProperty("after_date")
    private Date afterDate;

    @JsonProperty("contain_link")
    private Boolean containLink;

    @JsonProperty("contain_image")
    private Boolean containImage;

    @JsonProperty("contain_file")
    private Boolean containFile;

    private List<Long> accessibleChannelIds;
}
