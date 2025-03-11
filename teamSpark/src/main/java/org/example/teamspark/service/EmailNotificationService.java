package org.example.teamspark.service;

import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.Workspace;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@CommonsLog
public class EmailNotificationService {

    private final SesClient sesClient;

    EmailNotificationService(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    public static void send(SesClient client,
                            String sender,
                            String recipient,
                            String subject,
                            String bodyHTML) throws MessagingException {

        Destination destination = Destination.builder()
                .toAddresses(recipient)
                .build();

        Content content = Content.builder()
                .data(bodyHTML)
                .build();

        Content sub = Content.builder()
                .data(subject)
                .build();

        Body body = Body.builder()
                .html(content)
                .build();

        Message msg = Message.builder()
                .subject(sub)
                .body(body)
                .build();

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(destination)
                .message(msg)
                .source(sender)
                .build();

        try {
            System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
            client.sendEmail(emailRequest);

        } catch (SesException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * Sends an invitation email to a workspace member.
     *
     * @param user      the user to invite
     * @param workspace the workspace the user is invited to
     */
    public void sendWorkspaceMemberInviteEmail(User user, Workspace workspace) {
        String subject = "You're Invited to Join " + workspace.getName();
        String body = generateInviteEmailTemplate(user, workspace);
        String recipient = user.getEmail();

        try {
            log.info("Sending workspace invite email to " + recipient);

            send(sesClient, "your-email@example.com", recipient, subject, body);

            sesClient.sendEmail(SendEmailRequest.builder()
                    .destination(Destination.builder()
                            .toAddresses(recipient)
                            .build())
                    .message(Message.builder()
                            .subject(Content.builder()
                                    .data(subject)
                                    .charset("UTF-8")
                                    .build())
                            .body(Body.builder()
                                    .html(Content.builder()
                                            .data(body)
                                            .charset("UTF-8")
                                            .build())
                                    .build())
                            .build())
                    .source("your-email@example.com") // Replace with a verified SES email address
                    .build());

            log.info("Workspace invite email sent successfully to " + recipient);

        } catch (Exception e) {
            log.error("Failed to send workspace invite email to " + recipient, e);
        }
    }

    /**
     * Generates the email template for inviting a user to a workspace.
     *
     * @param user      the user to invite
     * @param workspace the workspace the user is invited to
     * @return the email content as a string
     */
    private String generateInviteEmailTemplate(User user, Workspace workspace) {
        return String.format(
                """
                        <html>
                            <body>
                                <h1>Hello %s,</h1>
                                <p>You have been invited to join the workspace <strong>%s</strong>.</p>
                                <p>Please click the link below to accept the invitation:</p>
                                <a href="%s">Join Workspace</a>
                                <p>If you did not request this invitation, you can safely ignore this email.</p>
                                <p>Best regards,<br>The Team</p>
                            </body>
                        </html>
                        """,
                user.getName(),
                workspace.getName(),
                "https://yourapp.com/workspace-invite/" + workspace.getId() // Replace with your actual invite link generation logic
        );
    }
}
