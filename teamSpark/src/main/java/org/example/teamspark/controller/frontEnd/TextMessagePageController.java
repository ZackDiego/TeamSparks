package org.example.teamspark.controller.frontEnd;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TextMessagePageController {

    @GetMapping("/textMessaging/{channelId}")
    public String homePage(@PathVariable String channelId, Model model) {
        model.addAttribute("channel_id", channelId);
        return "textMessaging";
    }
}
