package org.example.teamspark.controller;

import org.example.teamspark.controller.textMessage.TextMessageHistoryController;
import org.example.teamspark.dtoGenerator.MessageHistoryDtoGenerator;
import org.example.teamspark.exception.ElasticsearchFailedException;
import org.example.teamspark.exception.ResourceAccessDeniedException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.security.JwtAuthenticationFilter;
import org.example.teamspark.security.SecurityConfig;
import org.example.teamspark.service.MessageHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TextMessageHistoryController.class, excludeFilters =
@ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {JwtAuthenticationFilter.class}),
        excludeAutoConfiguration = {SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class TextMessageHistoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageHistoryService messageHistoryService;

    @BeforeEach
    public void init() throws IOException,
            ElasticsearchFailedException, ResourceAccessDeniedException {

        // set the return of service
        when(messageHistoryService.getMessagesByChannelId(anyLong(), any(User.class)))
                .thenReturn(MessageHistoryDtoGenerator.getMockMessageHistory());

        // Security context to simulate an authenticated user
        User user = mock(User.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    public void getMessagesByChannelId() throws Exception, ResourceAccessDeniedException {

        // test on channel 31
        mockMvc.perform(
                        get("/api/v1/channelId/31/message"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(MessageHistoryDtoGenerator.expectedMessageJson));

        Mockito.verify(messageHistoryService, Mockito.times(1))
                .getMessagesByChannelId(Mockito.eq(31L), any(User.class));
    }
}
