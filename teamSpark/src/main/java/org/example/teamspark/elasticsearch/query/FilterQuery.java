package org.example.teamspark.elasticsearch.query;

import com.fasterxml.jackson.databind.JsonNode;

public interface FilterQuery {
    JsonNode toJsonNode();
}
