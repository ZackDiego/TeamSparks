package org.example.teamspark.service;

import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.Workspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.ses.model.SesException;

import static org.junit.jupiter.api.Assertions.fail;

// Before testing, be sure to uncomment the send method in EmailNotificationService


@SpringBootTest
class EmailNotificationServiceTest {

    @Autowired
    private EmailNotificationService emailService;

    private User testUser;
    private Workspace testWorkspace;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setName("Zack Chiang");
        testUser.setEmail("zackykjdev@gmail.com");

        testWorkspace = new Workspace();
        testWorkspace.setName("Dev Team");
        testWorkspace.setId(Long.valueOf("12345"));
    }

    @Test
    void testSendWorkspaceMemberInviteEmail() {
        // Act
        try {
            emailService.sendWorkspaceMemberInviteEmail(testUser, testWorkspace);
        } catch (SesException e) {
            fail("❌ Failed to send email: " + e.awsErrorDetails().errorMessage());
        }
    }
}
