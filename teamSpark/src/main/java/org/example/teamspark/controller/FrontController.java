package org.example.teamspark.controller;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontController {
    @GetMapping("/")
    public String homePage(){
        return "index";
    }
}
