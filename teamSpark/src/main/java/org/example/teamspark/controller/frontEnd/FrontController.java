package org.example.teamspark.controller.frontEnd;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
public class FrontController {

    @Value("${host.name}")
    private String hostName;

    @Value("${turn.user.name}")
    private String turnUserName;

    @Value("${turn.password}")
    private String turnPassword;

    @GetMapping("/")
    public String root() {
        return "redirect:/homePage";
    }

    @GetMapping("/homePage")
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
    public String workspacePage(Model model) throws UnknownHostException {
        // Get the IP address of the current EC2 instance
        String ipAddress = InetAddress.getLocalHost().getHostAddress();
        model.addAttribute("ec2IpAddress", ipAddress);
        model.addAttribute("hostName", hostName);
        return "textMessaging";
    }

    @GetMapping("/channel/{channelId}/videoCall")
    public String channelVideoCallPage(Model model) {
        model.addAttribute("hostName", hostName);
        model.addAttribute("turnUserName", turnUserName);
        model.addAttribute("turnPassword", turnPassword);
        return "videoCall";
    }

    @GetMapping("/workspace/{workspaceId}/search")
    public String searchPage(Model model) {
        model.addAttribute("hostName", hostName);
        return "search";
    }
}
