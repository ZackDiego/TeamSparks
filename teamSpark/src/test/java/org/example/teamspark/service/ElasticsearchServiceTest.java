//package org.example.teamspark.service;
//
//import org.example.teamspark.TeamSparkApplication;
//import org.example.teamspark.data.dto.SearchCondition;
//import org.example.teamspark.model.channel.Channel;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.skyscreamer.jsonassert.JSONAssert;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//@SpringBootTest(classes = TeamSparkApplication.class, properties = {
//        "spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults=false",
//        "spring.jpa.hibernate.ddl-auto=none"
//})
//@Disabled
//public class ElasticsearchServiceTest {
//
//    @Autowired
//    ElasticsearchService elasticsearchService;
//
//    @Test
//    public void searchMessageWithCondition_with_fromId() throws Exception {
//
//        // input
//        Channel channel1 = new Channel();
//        channel1.setId(27L);
//        Channel channel2 = new Channel();
//        channel2.setId(28L);
//        Channel channel3 = new Channel();
//        channel3.setId(29L);
//        Channel channel4 = new Channel();
//        channel4.setId(31L);
//        Channel channel5 = new Channel();
//        channel5.setId(34L);
//
//        List<Channel> channels = List.of(channel1, channel2, channel3, channel4, channel5);
//
//        SearchCondition condition = new SearchCondition("hi", 18L, null, null, null, null, null, null);
//
//        // expected output
//        String expectedResponseBody = """
//                {"took":1,"timed_out":false,"_shards":{"total":5,"successful":5,"skipped":0,"failed":0},"hits":{"total":{"value":1,"relation":"eq"},"max_score":2.3187308,"hits":[{"_index":"channel-31","_type":"_doc","_id":"6fSBf48Bqh496SUPbpPM","_score":2.3187308,"_source":{"content":"Hi Zack<br>","message_id":null,"from_id":18,"from_name":"Alice Doe","plain_text_content":"Hi Zack","created_at":1715831140040,"contain_link":false,"file_url":null,"image_url":null}}]}}
//                """;
//
//        String responseBody = elasticsearchService.searchMessageWithCondition(channels, condition);
//        JSONAssert.assertEquals(expectedResponseBody, responseBody, false);
//    }
//
//    @Test
//    public void searchMessageWithCondition_with_fromId_and_beforeDate() throws Exception {
//
//        // input
//        Channel channel1 = new Channel();
//        channel1.setId(27L);
//        Channel channel2 = new Channel();
//        channel2.setId(28L);
//        Channel channel3 = new Channel();
//        channel3.setId(29L);
//        Channel channel4 = new Channel();
//        channel4.setId(31L);
//        Channel channel5 = new Channel();
//        channel5.setId(34L);
//
//        List<Channel> channels = List.of(channel1, channel2, channel3, channel4, channel5);
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = dateFormat.parse("2024-05-18");
//        SearchCondition condition = new SearchCondition("hi", 18L, null, date, null, null, null, null);
//
//        // expected output
//        String expectedResponseBody = """
//                {"took":1,"timed_out":false,"_shards":{"total":5,"successful":5,"skipped":0,"failed":0},"hits":{"total":{"value":1,"relation":"eq"},"max_score":2.3187308,"hits":[{"_index":"channel-31","_type":"_doc","_id":"6fSBf48Bqh496SUPbpPM","_score":2.3187308,"_source":{"content":"Hi Zack<br>","message_id":null,"from_id":18,"from_name":"Alice Doe","plain_text_content":"Hi Zack","created_at":1715831140040,"contain_link":false,"file_url":null,"image_url":null}}]}}
//                """;
//
//        String responseBody = elasticsearchService.searchMessageWithCondition(channels, condition);
//        JSONAssert.assertEquals(expectedResponseBody, responseBody, false);
//    }
//}
