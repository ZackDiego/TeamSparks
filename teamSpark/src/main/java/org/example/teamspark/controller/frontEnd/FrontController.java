package org.example.teamspark.controller.frontEnd;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontController {

    @Value("${host.name}")
    private String hostName;

    @GetMapping("/")
    public String homePage() {
        return "redirect:/login";
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
    public String workspacePage(Model model) {
        model.addAttribute("hostName", hostName);
        return "textMessaging";
    }

    @GetMapping("/channel/{channelId}/videoCall")
    public String channelVideoCallPage(Model model) {
        model.addAttribute("hostName", hostName);
        return "videoCall";
    }

    @GetMapping("/workspace/{workspaceId}/search")
    public String searchPage(Model model) {
        model.addAttribute("hostName", hostName);
        return "search";
    }
}
