package org.example.teamspark.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.teamspark.data.DataResponse;
import org.example.teamspark.data.dto.SearchCondition;
import org.example.teamspark.data.dto.message.MessageDto;
import org.example.teamspark.exception.ElasticsearchFailedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.SearchMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("api/v1/message/search")
public class SearchMessageController {
    private final SearchMessageService searchMessageService;

    public SearchMessageController(SearchMessageService searchMessageService) {
        this.searchMessageService = searchMessageService;
    }

    @PostMapping("")
    public ResponseEntity<?> searchMatchMessages(@RequestBody SearchCondition searchCondition) throws ElasticsearchFailedException, JsonProcessingException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<MessageDto> dtos = searchMessageService.getSearchMessages(user, searchCondition);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(dtos));
    }

}
