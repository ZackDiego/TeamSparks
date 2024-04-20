package org.example.teamspark.controller.channel;

import org.example.teamspark.data.DataResponse;
import org.example.teamspark.data.dto.ChannelDto;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api/v1")
public class ChannelController {
    private final ChannelService channelService;

    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    // get channel by workspace member id
    @GetMapping(value = "/member/{memberId}/channel")
    public ResponseEntity<?> handleGetChannelsByMemberId(
            @PathVariable Long memberId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ChannelDto> channelDtos = channelService.getChannelsByMemberId(user, memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(channelDtos));
    }

    // create channel
    @PostMapping(value = "/creator/{creatorId}/channel", consumes = {"application/json"})
    public ResponseEntity<?> handleCreateChannel(
            @PathVariable Long creatorId,
            @RequestBody ChannelDto channelDto) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ChannelDto createdChannelDto = channelService.createChannel(user, creatorId, channelDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(createdChannelDto));
    }

    // update channel
    @PutMapping(value = "/creator/{creatorId}/channel/{channelId}", consumes = {"application/json"})
    public ResponseEntity<?> handleUpdateChannel(
            @PathVariable Long creatorId,
            @PathVariable Long channelId,
            @RequestBody ChannelDto channelDto) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ChannelDto updatedChannelDto = channelService.updateChannel(user, creatorId, channelId, channelDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(updatedChannelDto));
    }

    // delete channel
    @DeleteMapping("/creator/{creatorId}/channel/{channelId}")
    public ResponseEntity<?> handleDeleteChannel(
            @PathVariable Long creatorId,
            @PathVariable Long channelId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        channelService.deleteChannel(user, creatorId, channelId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
