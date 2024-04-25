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
import java.util.Map;

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
    @PostMapping(value = "/channel", consumes = {"application/json"})
    public ResponseEntity<?> handleCreateChannel(
            @RequestBody ChannelDto channelDto) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long createdChannelId = channelService.createChannel(user, channelDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(Map.of("id", createdChannelId)));
    }

    // update channel
    @PutMapping(value = "/channel/{channel_id}", consumes = {"application/json"})
    public ResponseEntity<?> handleUpdateChannel(
            @PathVariable("channel_id") Long channelId,
            @RequestBody ChannelDto channelDto) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ChannelDto updatedChannelDto = channelService.updateChannel(user, channelId, channelDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(updatedChannelDto));
    }

    // delete channel
    @DeleteMapping("/channel/{channel_id}")
    public ResponseEntity<?> handleDeleteChannel(
            @PathVariable("channel_id") Long channelId) throws ResourceAccessDeniedException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        channelService.deleteChannel(user, channelId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
