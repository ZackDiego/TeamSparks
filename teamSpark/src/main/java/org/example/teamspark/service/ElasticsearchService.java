package org.example.teamspark.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.tomcat.util.codec.binary.Base64;
import org.example.teamspark.data.dto.SearchCondition;
import org.example.teamspark.data.dto.message.MessageDto;
import org.example.teamspark.data.dto.message.MessageId;
import org.example.teamspark.elasticsearch.query.*;
import org.example.teamspark.exception.ElasticsearchFailedException;
import org.example.teamspark.model.channel.Channel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@CommonsLog
public class ElasticsearchService {
    RestTemplate restTemplate;

    @Value("${elasticsearch.username}")
    private String ESUserName;

    @Value("${elasticsearch.password}")
    private String ESPassword;

    @Value("${elasticsearch.url}")
    private String ESUrl;

    public ElasticsearchService() {
        this.restTemplate = new RestTemplate();
    }

    public static List<MessageDto> mapSearchResultToMessageDocuments(String responseBody) throws JsonProcessingException {
        // map response body to
        ObjectMapper objectMapper = new ObjectMapper();

        // Deserialize the Elasticsearch response body to a JsonNode
        JsonNode responseObj = objectMapper.readTree(responseBody);

        // Extract the hits from the responseBody
        JsonNode hits = responseObj.get("hits").get("hits");

        Stream<JsonNode> hitsStream = StreamSupport.stream(hits.spliterator(), false);

        // Map each hit to your model class using ObjectMapper and collect them into a List
        return hitsStream
                .map(hit -> {
                    JsonNode source = hit.get("_source");
                    MessageDto dto = new MessageDto();
                    try {
                        dto = objectMapper.treeToValue(hit.get("_source"), MessageDto.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    dto.setMessageId(new MessageId(hit.get("_index").asText(), hit.get("_id").asText()));
                    return dto;
                })
                .toList();
    }

    public void createIndex(String indexName) throws JsonProcessingException {
        String indexEndpoint = ESUrl + "/" + indexName;

        // Create HttpHeaders with authentication
        HttpHeaders headers = createHeaders(ESUserName, ESPassword);

        log.info("Index does not exist. Creating index in Elasticsearch: " + indexName);
        String requestBody = "{}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> putResponse = restTemplate.exchange(indexEndpoint, HttpMethod.PUT, requestEntity, String.class);

        // Check if the index creation was successful
        if (putResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Index created in Elasticsearch: " + indexName);
        } else {
            log.error("Failed to create index: " + indexName);
        }
    }

    public MessageId addDocumentToIndex(String indexName, String json) throws JsonProcessingException, ElasticsearchFailedException {

        String indexEndpoint = ESUrl + "/" + indexName + "/_doc";

        // Create HttpHeaders with authentication
        HttpHeaders headers = createHeaders(ESUserName, ESPassword);

        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        // Send POST request to add message to index
        ResponseEntity<String> response = restTemplate.exchange(indexEndpoint, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Message added to index: " + indexName);

            // return saved document id
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            MessageId messageId = new MessageId();
            messageId.setIndexName(rootNode.path("_index").asText());
            messageId.setDocumentId(rootNode.path("_id").asText());
            return messageId;
        } else {
            throw new ElasticsearchFailedException("Failed to add message to index: " + indexName);
        }
    }

    public String getDocumentsByIndexName(String indexName) throws ElasticsearchFailedException {

        // Create HttpHeaders with authentication
        HttpHeaders headers = createHeaders(ESUserName, ESPassword);

        // Get the index
        String searchUrl = ESUrl + "/" + indexName + "/_search?size=10000";

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(searchUrl, HttpMethod.GET, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new ElasticsearchFailedException("Failed to get data from index: " + indexName + " becuase of " + e.getMessage());
        }
    }

    private HttpHeaders createHeaders(String username, String password) {
        String auth = username + ":" + password;
        byte[] plainCredsBytes = auth.getBytes();
        String base64Creds = Base64.encodeBase64String(plainCredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.add("Authorization", "Basic " + base64Creds);

        return headers;
    }

    public String searchMessageWithCondition(List<Channel> channels, SearchCondition condition) throws ElasticsearchFailedException, JsonProcessingException {

        if (condition.getChannelId() == null) {
            String indices = channels.stream()
                    .map(channel -> "channel-" + channel.getId())
                    .collect(Collectors.joining(","));

            String searchUrl = ESUrl + "/" + indices + "/_search?size=10000";

            ResponseEntity<String> response = getElasticsearchSearchResult(condition, searchUrl);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ElasticsearchFailedException("Failed to get data from index: " + indices);
            }
        } else {
            String index = "channel-" + condition.getChannelId();
            String searchUrl = ESUrl + "/" + index + "/_search?size=10000";

            ResponseEntity<String> response = getElasticsearchSearchResult(condition, searchUrl);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ElasticsearchFailedException("Failed to get data from index: " + index);
            }
        }
    }

    private ResponseEntity<String> getElasticsearchSearchResult(SearchCondition condition, String searchUrl) throws
            ElasticsearchFailedException, JsonProcessingException {

        // Create HttpHeaders with authentication
        HttpHeaders headers = createHeaders(ESUserName, ESPassword);

        ObjectMapper objectMapper = new ObjectMapper();

        MatchQuery matchQuery = new MatchQuery(condition.getSearchKeyword());
        MustQuery mustQuery = new MustQuery(matchQuery);

        // Filter
        List<JsonNode> filterQueries = new ArrayList<>();
        if (condition.getFromId() != null) {
            TermFilter termFilter = new TermFilter("from_id", condition.getFromId());
            filterQueries.add(termFilter.toJsonNode());
        }
        if (condition.getBeforeDate() != null) {
            RangeFilter rangeFilter = new RangeFilter("created_at", condition.getBeforeDate().getTime(), "lte");
            filterQueries.add(rangeFilter.toJsonNode());
        }
        if (condition.getAfterDate() != null) {
            RangeFilter rangeFilter = new RangeFilter("created_at", condition.getAfterDate().getTime(), "gte");
            filterQueries.add(rangeFilter.toJsonNode());
        }
        if (condition.getContainLink() != null) {
            TermFilter termFilter = new TermFilter("contain_link", condition.getContainLink());
            filterQueries.add(termFilter.toJsonNode());
        }
        if (condition.getContainFile() != null && condition.getContainFile()) {
            ExistsFilter existsFilter = new ExistsFilter("file_url");
            filterQueries.add(existsFilter.toJsonNode());
        }
        if (condition.getContainImage() != null) {
            ExistsFilter existsFilter = new ExistsFilter("image_url");
            filterQueries.add(existsFilter.toJsonNode());
        }

        BoolQuery boolQuery = new BoolQuery(mustQuery, filterQueries);
        Query query = new Query(boolQuery);
        ElasticsearchRequestBody requestBody = new ElasticsearchRequestBody(query);

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, headers);

        try {
            return restTemplate.exchange(searchUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            throw new ElasticsearchFailedException(e.getMessage());
        }
    }
}
