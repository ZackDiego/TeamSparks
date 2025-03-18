//package org.example.teamspark.service;
//
//import org.example.teamspark.TeamSparkApplication;
//import org.example.teamspark.data.dto.ChannelDto;
//import org.example.teamspark.data.dto.UserDto;
//import org.example.teamspark.data.dto.WorkspaceMemberDto;
//import org.example.teamspark.exception.ResourceAccessDeniedException;
//import org.example.teamspark.model.user.User;
//import org.example.teamspark.model.workspace.Workspace;
//import org.example.teamspark.model.workspace.WorkspaceMember;
//import org.example.teamspark.repository.ChannelMemberRepository;
//import org.example.teamspark.repository.ChannelRepository;
//import org.example.teamspark.repository.WorkspaceMemberRepository;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest(classes = TeamSparkApplication.class, properties = {
//        "spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults=false",
//        "spring.jpa.hibernate.ddl-auto=none"
//})
//@Disabled
//public class ChannelServiceTest {
//    @Autowired
//    ChannelService channelService;
//
//    @Autowired
//    ChannelRepository channelRepository;
//
//    @MockBean
//    ChannelMemberRepository channelMemberRepository;
//
//    @MockBean
//    WorkspaceMemberRepository workspaceMemberRepository;
//
//    @MockBean
//    ElasticsearchService elasticsearchService;
//
//    @Test
//    public void test_getChannelsByMemberId() throws ResourceAccessDeniedException, ParseException {
//
//        // input
//        User user = new User(49L, "Yang Dou", "yang123456@gmail.com", "$2a$10$EYMLxBGcBpDpsZ5rJ5ubseHpAI6XDYrm7codaR82fRzQxJwuf8IVK", "https://d328zl7ag0tjva.cloudfront.net/userAvatar-8541971559382274402.png", null);
//        Long memberId = 47L;
//
//        // set workspaceMemberRepository.findById
//        String dateString = "2024-05-02 08:24:58.789";
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        Date date = dateFormat.parse(dateString);
//        Workspace workspace = new Workspace(12L, "AppWorks School", date, "https://d328zl7ag0tjva.cloudfront.net/workspaceAvatar-8584640713015026612.png");
//        String joinedAtString = "2024-05-14 17:15:53.107";
//        Date joinedAt = dateFormat.parse(joinedAtString);
//        WorkspaceMember member = new WorkspaceMember(47L, workspace, user, false, joinedAt);
//
//        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member));
//        List<ChannelDto> dtos = channelService.getChannelsByMemberId(user, memberId);
//
//        // expected output
//        UserDto user1 = new UserDto(2L, "Zack Chiang", null, "https://d328zl7ag0tjva.cloudfront.net/userAvatar-6303800839868657413.jpeg", null);
//        UserDto user2 = new UserDto(4L, "Alice Doe", null, "https://d328zl7ag0tjva.cloudfront.net/userAvatar-4791412374790159758.jpeg", null);
//        UserDto user3 = new UserDto(5L, "Bob Aaron", null, "https://d328zl7ag0tjva.cloudfront.net/userAvatar-5970089899955583758.jpeg", null);
//        UserDto user4 = new UserDto(34L, "Cindy Clark", null, "https://d328zl7ag0tjva.cloudfront.net/userAvatar-6208781797930427168.jpg", null);
//        UserDto user5 = new UserDto(35L, "Jennifer Martinez", null, "https://d328zl7ag0tjva.cloudfront.net/userAvatar-4705572546871529402.jpg", null);
//        UserDto user6 = new UserDto(3L, "John Barker", null, "https://d328zl7ag0tjva.cloudfront.net/userAvatar-5265498375455465293.jpg", null);
//        UserDto user7 = new UserDto(47L, "Chih-Yu Chang", null, "https://d328zl7ag0tjva.cloudfront.net/userAvatar-8046549911442566677.png", null);
//        UserDto user8 = new UserDto(48L, "Yang Dou", null, "https://d328zl7ag0tjva.cloudfront.net/userAvatar-4763254442006953897.png", null);
//        UserDto user9 = new UserDto(49L, "Yang Dou", null, "https://d328zl7ag0tjva.cloudfront.net/userAvatar-8541971559382274402.png", null);
//
//        WorkspaceMemberDto member1 = new WorkspaceMemberDto(16L, user1, true, null);
//        WorkspaceMemberDto member2 = new WorkspaceMemberDto(18L, user2, false, null);
//        WorkspaceMemberDto member3 = new WorkspaceMemberDto(19L, user3, false, null);
//        WorkspaceMemberDto member4 = new WorkspaceMemberDto(30L, user4, false, null);
//        WorkspaceMemberDto member5 = new WorkspaceMemberDto(31L, user5, false, null);
//        WorkspaceMemberDto member6 = new WorkspaceMemberDto(43L, user6, false, null);
//        WorkspaceMemberDto member7 = new WorkspaceMemberDto(44L, user7, false, null);
//        WorkspaceMemberDto member8 = new WorkspaceMemberDto(45L, user8, false, null);
//        WorkspaceMemberDto member9 = new WorkspaceMemberDto(47L, user9, false, null);
//
//        List<WorkspaceMemberDto> members = Arrays.asList(member1, member2, member3, member4, member5, member6, member7, member8, member9);
//
//        String createdAtString = "2024-05-02 08:25:27.039";
//        Date createdAt = dateFormat.parse(createdAtString);
//        ChannelDto dto1 = new ChannelDto(27L, 12L, "Back-End", createdAt, false, members);
//
//        List<ChannelDto> expectedDtos = List.of(dto1);
//
//        assertEquals(expectedDtos, dtos);
//    }
//
//}
