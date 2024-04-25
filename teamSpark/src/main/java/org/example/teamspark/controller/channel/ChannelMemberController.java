package org.example.teamspark.controller.channel;

import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.ChannelMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/v1/channel/{channelId}/member")
public class ChannelMemberController {

    private final ChannelMemberService channelMemberService;

    public ChannelMemberController(ChannelMemberService channelMemberService) {
        this.channelMemberService = channelMemberService;
    }

    // add channel member
    @PostMapping(value = "/{wsMemberId}")
    public ResponseEntity<?> handleCreateChannel(
            @PathVariable Long channelId,
            @PathVariable Long wsMemberId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        channelMemberService.addChannelMember(user, channelId, wsMemberId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // remove channel member
    @DeleteMapping("/{wsMemberId}")
    public ResponseEntity<?> handleDeleteChannel(
            @PathVariable Long channelId,
            @PathVariable Long wsMemberId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        channelMemberService.removeChannelMember(user, channelId, wsMemberId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
