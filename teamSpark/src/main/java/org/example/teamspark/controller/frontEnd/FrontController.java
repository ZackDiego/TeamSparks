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

    @GetMapping("/videoCall/{channelId}")
    public String homePage(@PathVariable String channelId, Model model) {
        model.addAttribute("channel_id", channelId);
        return "videoCall";
    }
}
