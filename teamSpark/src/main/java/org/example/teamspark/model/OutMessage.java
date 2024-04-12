package org.example.teamspark.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutMessage {

    private String from;

    private String content;

    private Date time = new Date();

    public OutMessage(String content) {
        this.content = content;
    }
}
