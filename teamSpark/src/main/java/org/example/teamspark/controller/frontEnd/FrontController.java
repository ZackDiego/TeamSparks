package org.example.teamspark.controller.frontEnd;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/user/{userId}/workspace/{workspaceId}")
    public String workspacePage(@PathVariable String userId, @PathVariable String workspaceId, Model model) {
        model.addAttribute("user_id", userId);
        model.addAttribute("workspace_id", userId);
        return "textMessaging";
    }
}
