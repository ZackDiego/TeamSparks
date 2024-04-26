package org.example.teamspark.data.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InMessageDto {

    @NotBlank
    @JsonProperty("channel_id")
    private Long channelId;

    private MessageDto message;
}
