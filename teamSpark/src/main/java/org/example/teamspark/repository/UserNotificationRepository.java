package org.example.teamspark.repository;

import org.example.teamspark.model.user.User;
import org.example.teamspark.model.user.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByUser(User user);
}
