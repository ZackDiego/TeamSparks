package org.example.teamspark.model.event;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.model.user.User;

import java.io.Serializable;

@Entity
@Table(name = "event_participant")
@Data
@NoArgsConstructor
@IdClass(EventParticipant.EventParticipantId.class)
public class EventParticipant implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NoArgsConstructor
    @Data
    public static class EventParticipantId implements Serializable {

        private Long event;

        private Long user;

    }

}
