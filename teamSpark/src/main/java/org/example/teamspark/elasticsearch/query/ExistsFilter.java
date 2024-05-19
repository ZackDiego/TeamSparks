package org.example.teamspark.elasticsearch.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistsFilter implements FilterQuery {
    private String field;

    @Override
    public JsonNode toJsonNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        ObjectNode existsNode = mapper.createObjectNode();
        existsNode.put("field", field);
        node.set("exists", existsNode);
        return node;
    }
}
