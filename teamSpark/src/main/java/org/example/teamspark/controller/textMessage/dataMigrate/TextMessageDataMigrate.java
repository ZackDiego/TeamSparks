//package org.example.teamspark.controller.textMessage.dataMigrate;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.example.teamspark.data.dto.message.ChannelMessageHistoryDto;
//import org.example.teamspark.data.dto.message.MessageDto;
//import org.example.teamspark.data.dto.message.MessageId;
//import org.example.teamspark.repository.MessageHistoryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//import java.util.stream.StreamSupport;
//
//@Controller
//public class TextMessageDataMigrate {
//    @Autowired
//    MessageHistoryRepository messageHistoryRepository;
//    @Value("${elasticsearch.username}")
//    private String ESUserName;
//    @Value("${elasticsearch.password}")
//    private String ESPassword;
//    @Value("${elasticsearch.url}")
//    private String ESUrl;
//
//    @GetMapping("/dataMigrate")
//    public void migrate() throws JsonProcessingException {
//        RestTemplate restTemplate = new RestTemplate();
//
//        // Create HttpHeaders with authentication
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBasicAuth(ESUserName, ESPassword);
//
//        // Query to fetch all documents from all indices
//        String queryJson = "{ \"query\": { \"match_all\": {} }, \"size\": 10000 }";
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(queryJson, headers);
//        String dataResponse = restTemplate.postForObject(ESUrl + "/channel-*/_search", requestEntity, String.class);
//
//        List<MessageDto> messageDtos = mapSearchResultToMessageDocuments(dataResponse);
//
//        // Insert to MongoDb
//        messageDtos.forEach(System.out::println);
//
//        // Transform to channelMessageHistoryDto
//        List<ChannelMessageHistoryDto> channelMessageHistoryDtos = messageDtos.stream()
//                .collect(Collectors.groupingBy(msg -> msg.getMessageId().getChannelId()))
//                .entrySet().stream()
//                .map(entry -> {
//                    Long channelId = entry.getKey();
//                    List<MessageDto> messages = entry.getValue();
//
//                    ChannelMessageHistoryDto channelMessageHistoryDto = new ChannelMessageHistoryDto();
//                    channelMessageHistoryDto.setChannelId(channelId);
//                    channelMessageHistoryDto.setMessages(messages);
//
//                    return channelMessageHistoryDto;
//                })
//                .toList();
//
//        channelMessageHistoryDtos.forEach(System.out::println);
//
//        try {
//            messageHistoryRepository.saveAll(channelMessageHistoryDtos);
//        } catch (Exception e) {
//            // Handle exception
//            e.printStackTrace();
//        }
//    }
//
//    @GetMapping("/getAllIndices")
//    public void getAllIndices() {
//        RestTemplate restTemplate = new RestTemplate();
//
//// Create HttpHeaders with authentication
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBasicAuth(ESUserName, ESPassword);
//
//// Elasticsearch URL for fetching all indices
//        String url = ESUrl + "/_cat/indices?format=json";
//
//        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
//        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
//
//// Get the response containing all index information
//        String indicesResponse = responseEntity.getBody();
//        System.out.println(indicesResponse);
//    }
//
//    @GetMapping("/removeIndex")
//    public void removeIndex() {
//        RestTemplate restTemplate = new RestTemplate();
//
//// Create HttpHeaders with authentication
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBasicAuth(ESUserName, ESPassword);
//
//// Elasticsearch URL for fetching all indices
//        String url = ESUrl + "/channel-null";
//
//        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
//        // Send DELETE request
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
//
//        System.out.println("Response: " + response.getBody());
//    }
//
//    private List<MessageDto> mapSearchResultToMessageDocuments(String responseBody) throws JsonProcessingException {
//        // map response body to
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        // Deserialize the Elasticsearch response body to a JsonNode
//        JsonNode responseObj = objectMapper.readTree(responseBody);
//
//        // Extract the hits from the responseBody
//        JsonNode hits = responseObj.get("hits").get("hits");
//
//        Stream<JsonNode> hitsStream = StreamSupport.stream(hits.spliterator(), false);
//
//        // Map each hit to your model class using ObjectMapper and collect them into a List
//        return hitsStream
//                .map(hit -> {
//
//                    MessageDto dto = new MessageDto();
//                    try {
//                        dto = objectMapper.treeToValue(hit.get("_source"), MessageDto.class);
//                    } catch (JsonProcessingException e) {
//                        System.out.println(hit.get("_source"));
//                        throw new RuntimeException(e);
//                    }
//
//                    // Transform the data
//                    try {
//                        dto.setMessageId(new MessageId(Long.parseLong(hit.get("_index").asText().replace("channel-", "")), hit.get("_id").asText()));
//                    } catch (NumberFormatException e) {
//                        System.out.println(hit.get("_index"));
//                        System.out.println(hit.get("_id"));
//                        throw new RuntimeException(e);
//                    }
//                    return dto;
//                })
//                .toList();
//    }
//}
