package org.example.teamspark.dtoGenerator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.teamspark.data.dto.message.ChannelMessageHistoryDto;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class MessageHistoryDtoGenerator {
    public static final String expectedMessageJson = """
            {
                "data": {
                    "channel_id": 31,
                    "is_private": true,
                    "messages": [
                        {
                            "content": "Hey Zack, how do you find the company culture here?",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "3PRTf48Bqh496SUPvpPq"
                            },
                            "from_id": 18,
                            "from_name": "Alice Doe",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">Hey Zack, how do you find the company culture here?</span><br></p>",
                            "created_at": "2024-05-02T01:27:14.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "Hi Alice, I think the company culture is collaborative and supportive.",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "3fRTf48Bqh496SUPv5Mm"
                            },
                            "from_id": 16,
                            "from_name": "Zack Chiang",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">Hi Alice, I think the company culture is collaborative and supportive.</span><br></p>",
                            "created_at": "2024-05-02T02:15:32.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "That's great to hear. How do you balance your daily tasks and deadlines?",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "3vRTf48Bqh496SUPv5Nb"
                            },
                            "from_id": 18,
                            "from_name": "Alice Doe",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">That's great to hear. How do you balance your daily tasks and deadlines?</span><br></p>",
                            "created_at": "2024-05-03T03:42:18.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "I prioritize tasks based on deadlines and importance, and I use time management techniques to stay organized.",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "3_RTf48Bqh496SUPv5OP"
                            },
                            "from_id": 16,
                            "from_name": "Zack Chiang",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">I prioritize tasks based on deadlines and importance, and I use time management techniques to stay organized.</span><br></p>",
                            "created_at": "2024-05-03T03:58:55.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "Interesting. Do you find the workload manageable?",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "4PRTf48Bqh496SUPwJNN"
                            },
                            "from_id": 18,
                            "from_name": "Alice Doe",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">Interesting. Do you find the workload manageable?</span><br></p>",
                            "created_at": "2024-05-04T04:19:27.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "Yes, it can be challenging at times, but overall, it's manageable with proper planning and communication.",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "4fRTf48Bqh496SUPwJN_"
                            },
                            "from_id": 16,
                            "from_name": "Zack Chiang",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">Yes, it can be challenging at times, but overall, it's manageable with proper planning and communication.</span><br></p>",
                            "created_at": "2024-05-04T04:44:09.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "How about the team dynamics? Do you feel like you're part of a cohesive team?",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "4vRTf48Bqh496SUPwJPG"
                            },
                            "from_id": 18,
                            "from_name": "Alice Doe",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">How about the team dynamics? Do you feel like you're part of a cohesive team?</span><br></p>",
                            "created_at": "2024-05-05T05:37:21.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "Absolutely, the team is supportive, and we collaborate well to achieve our goals.",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "4_RTf48Bqh496SUPwJP7"
                            },
                            "from_id": 16,
                            "from_name": "Zack Chiang",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">Absolutely, the team is supportive, and we collaborate well to achieve our goals.</span><br></p>",
                            "created_at": "2024-05-05T05:58:47.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "That's reassuring. Any tips for staying motivated during busy periods?",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "5PRTf48Bqh496SUPwZMr"
                            },
                            "from_id": 18,
                            "from_name": "Alice Doe",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">That's reassuring. Any tips for staying motivated during busy periods?</span><br></p>",
                            "created_at": "2024-05-06T06:02:05.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "Setting short-term goals, taking breaks, and celebrating achievements help me stay motivated.",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "5fRTf48Bqh496SUPwZNa"
                            },
                            "from_id": 16,
                            "from_name": "Zack Chiang",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">Setting short-term goals, taking breaks, and celebrating achievements help me stay motivated.</span><br></p>",
                            "created_at": "2024-05-06T06:33:29.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "Thanks for sharing, Zack. It's always helpful to learn from others' experiences.",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "5vRTf48Bqh496SUPwZPX"
                            },
                            "from_id": 18,
                            "from_name": "Alice Doe",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">Thanks for sharing, Zack. It's always helpful to learn from others' experiences.</span><br></p>",
                            "created_at": "2024-05-07T07:10:18.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "You're welcome, Alice. Feel free to reach out if you need any advice or support.",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "5_RTf48Bqh496SUPwpNV"
                            },
                            "from_id": 16,
                            "from_name": "Zack Chiang",
                            "plain_text_content": "<p style=\\"line-height: 1;\\"><span style=\\"font-size: 14px;\\">You're welcome, Alice. Feel free to reach out if you need any advice or support.</span><br></p>",
                            "created_at": "2024-05-07T07:48:50.000+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "Hi Alice<br>",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "6PSBf48Bqh496SUPNZO7"
                            },
                            "from_id": 16,
                            "from_name": "Zack Chiang",
                            "plain_text_content": "Hi Alice",
                            "created_at": "2024-05-16T03:45:25.430+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        },
                        {
                            "content": "Hi Zack<br>",
                            "message_id": {
                                "indexName": "channel-31",
                                "documentId": "6fSBf48Bqh496SUPbpPM"
                            },
                            "from_id": 18,
                            "from_name": "Alice Doe",
                            "plain_text_content": "Hi Zack",
                            "created_at": "2024-05-16T03:45:40.040+00:00",
                            "contain_link": false,
                            "file_url": null,
                            "image_url": null
                        }
                    ]
                }
            }
            """;

    public static ChannelMessageHistoryDto getMockMessageHistory() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new ClassPathResource("./test/messages.json").getFile(), new TypeReference<>() {
        });
    }
}
