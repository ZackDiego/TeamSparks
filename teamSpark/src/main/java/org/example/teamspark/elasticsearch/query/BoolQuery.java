package org.example.teamspark.elasticsearch.query;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoolQuery {
    private MustQuery must;
    private List<JsonNode> filter;
}
