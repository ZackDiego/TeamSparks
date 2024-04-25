package org.example.teamspark.controller.frontEnd;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontController {

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "loginPage";
    }

    @GetMapping("/user")
    public String userPage() {
        return "user";
    }

    @GetMapping("/workspace/{workspaceId}")
    public String workspacePage() {
        return "textMessaging";
    }

    @GetMapping("/channel/{channelId}/videoCall")
    public String channelVideoCallPage() {
        return "videoCall";
    }
}
