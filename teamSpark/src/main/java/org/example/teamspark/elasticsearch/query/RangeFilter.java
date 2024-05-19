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
public class RangeFilter implements FilterQuery {
    private String field;
    private Long value;
    private String operator;

    @Override
    public JsonNode toJsonNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        ObjectNode rangeNode = mapper.createObjectNode();
        ObjectNode fieldNode = mapper.createObjectNode();
        fieldNode.put(operator, value);
        rangeNode.set(field, fieldNode);
        node.set("range", rangeNode);
        return node;
    }
}
