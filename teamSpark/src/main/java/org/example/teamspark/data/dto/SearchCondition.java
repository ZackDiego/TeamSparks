package org.example.teamspark.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCondition {
    private Long SenderId;
    private Long ChannelId;
    private String senderName;
    private String channelName;
    private Date beforeDate;
    private Date afterDate;
    private Boolean containLink;
    private Boolean containImage;
    private Boolean containFile;
    private String searchKeyword;
}
