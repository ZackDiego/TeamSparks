package org.example.teamspark.controller.textMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.teamspark.data.DataResponse;
import org.example.teamspark.data.dto.MessageHistoryDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.MessageHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/v1/channelId/{channelId}/message")
public class TextMessageHistoryController {

    private final MessageHistoryService messageHistoryService;

    @Autowired
    public TextMessageHistoryController(MessageHistoryService messageHistoryService) {
        this.messageHistoryService = messageHistoryService;
    }

    // get message history by channel id
    @GetMapping("")
    public ResponseEntity<?> getMessagesByChannelId(@PathVariable Long channelId) throws ResourceAccessDeniedException, JsonProcessingException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        MessageHistoryDto messageHistoryDto = messageHistoryService.getMessagesByChannelId(channelId, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(messageHistoryDto));
    }

//    // update message by id
//    @PutMapping(value = "/{messageId}", consumes = {"application/json"})
//    public ResponseEntity<?> updateChannelMessage(@PathVariable Long channelId, @PathVariable Long messageId,
//                                                  @RequestBody MessageDto messageDto) throws ResourceAccessDeniedException {
//
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        messageHistoryService.updateMessageByMessageId(channelId, messageId, messageDto, user);
//
//        return ResponseEntity.ok().build();
//    }
//
//    // delete role
//    @DeleteMapping(value = "/{messageId}")
//    public ResponseEntity<?> deleteChannelMessage(@PathVariable Long channelId, @PathVariable Long messageId) throws ResourceAccessDeniedException {
//
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        messageHistoryService.deleteMessageByMessageId(channelId, messageId, user);
//
//        return ResponseEntity.ok().build();
//    }

}

