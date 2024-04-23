package org.example.teamspark.controller;

import org.example.teamspark.data.dto.MessageDto;
import org.example.teamspark.data.dto.SearchCondition;
import org.example.teamspark.model.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("api/v1/message/search")
public class SearchMessageController {
    private final

    @PostMapping("")
    public List<MessageDto> searchMatchMessages(@RequestBody SearchCondition searchCondition) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


    }

}
