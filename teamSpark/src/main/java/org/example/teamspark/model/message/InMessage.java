package org.example.teamspark.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InMessage {

    private String from;

    private String to;

    private String content;

    private Date time;

    public InMessage(String content) {
        this.content = content;
    }
}
