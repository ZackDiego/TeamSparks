package org.example.teamspark.controller.channel;

import org.example.teamspark.data.dto.WorkspaceMemberDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.ChannelMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/v1/creator/{creator_id}/channel/{channel_id}/member")
public class ChannelMemberController {

    private final ChannelMemberService channelMemberService;

    public ChannelMemberController(ChannelMemberService channelMemberService) {
        this.channelMemberService = channelMemberService;
    }

    // add channel member
    @PostMapping(value = "", consumes = {"application/json"})
    public ResponseEntity<?> handleCreateChannel(
            @PathVariable("creator_id") Long creatorId,
            @PathVariable("channel_id") Long channelId,
            @RequestBody WorkspaceMemberDto workspaceMemberDto) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        channelMemberService.addChannelMember(user, creatorId, channelId, workspaceMemberDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // remove channel member
    @DeleteMapping("")
    public ResponseEntity<?> handleDeleteChannel(
            @PathVariable("creator_id") Long creatorId,
            @PathVariable("channel_id") Long channelId,
            @RequestBody WorkspaceMemberDto workspaceMemberDto) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        channelMemberService.removeChannelMember(user, creatorId, channelId, workspaceMemberDto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
