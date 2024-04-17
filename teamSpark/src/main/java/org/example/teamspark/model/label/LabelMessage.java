package org.example.teamspark.model.label;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "label_message")
@Data
@NoArgsConstructor
@IdClass(LabelMessage.LabelMessageId.class)
public class LabelMessage {

    @Id
    @Column(name = "label_id")
    private Long labelId;

    @Id
    @Column(name = "message_index_name")
    private String messageIndexName;

    @Id
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "label_id", insertable = false, updatable = false)
    private Label label;

    @NoArgsConstructor
    @Data
    public static class LabelMessageId implements Serializable {

        private Long labelId;

        private String messageIndexName;

        private Long messageId;
    }
}