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
public class TermFilter implements FilterQuery {
    private String field;
    private Object value;

    @Override
    public JsonNode toJsonNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        ObjectNode termNode = mapper.createObjectNode();
        termNode.put(field, value.toString());
        node.set("term", termNode);
        return node;
    }
}
