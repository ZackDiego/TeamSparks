package org.example.teamspark.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.tomcat.util.codec.binary.Base64;
import org.example.teamspark.exception.ElasticsearchFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public void createIndexIfNotExists(String indexName) throws JsonProcessingException {
        String indexEndpoint = ESUrl + "/" + indexName;

        // Create HttpHeaders with authentication
        HttpHeaders headers = createHeaders(ESUserName, ESPassword);

        // Perform a HEAD request to check if the index exists
        ResponseEntity<String> headResponse = restTemplate.exchange(indexEndpoint, HttpMethod.HEAD, new HttpEntity<>(headers), String.class);

        // Check if the index exists
        if (headResponse.getStatusCode() == HttpStatus.OK) {
            log.info("Index already exists in Elasticsearch: " + indexName);
            return;
        }

        // If the index does not exist, create it
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

    public String addDocumentToIndex(String indexName, String json) throws JsonProcessingException, ElasticsearchFailedException {

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
            return rootNode.path("_id").asText();
        } else {
            throw new ElasticsearchFailedException("Failed to add message to index: " + indexName);
        }
    }

    public String getDocumentsByIndexName(String indexName) throws JsonProcessingException {
        String checkExistsUrl = ESUrl + "/" + indexName;

        // Create HttpHeaders with authentication
        HttpHeaders headers = createHeaders(ESUserName, ESPassword);

        // Perform a HEAD request to check if the index exists
        ResponseEntity<String> headResponse = restTemplate.exchange(checkExistsUrl, HttpMethod.HEAD, new HttpEntity<>(headers), String.class);

        // Check if the index exists
        if (headResponse.getStatusCode() != HttpStatus.OK) {
            log.info("Index already exists in Elasticsearch: " + indexName);
            throw new EntityNotFoundException("Index " + indexName + " not found");
        }

        // Get the index
        String searchUrl = ESUrl + "/" + indexName + "/_search?size=10000";

        String requestBody = "{\"query\": {\"match_all\": {}}}"; // all documents
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(searchUrl, HttpMethod.GET, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to get data from index: " + indexName);
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
}
