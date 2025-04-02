package org.example.teamspark.repository;

import org.example.teamspark.data.dto.SearchCondition;
import org.example.teamspark.data.dto.message.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageHistoryRepositoryImpl implements CustomMessageHistoryRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MessageHistoryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<MessageDto> findMessagesWithSearchCondition(SearchCondition condition) {

        // Build criteria for document-level filtering
        Criteria docCriteria = new Criteria();
        if (condition.getAccessibleChannelIds() != null) {
            docCriteria.and("channelId").in(condition.getAccessibleChannelIds());
        }

        // Build message-level criteria
        Criteria msgCriteria = new Criteria();
        if (condition.getSearchKeyword() != null && !condition.getSearchKeyword().isEmpty()) {
            String keywordRegex = ".*" + condition.getSearchKeyword() + ".*";
            msgCriteria.and("messages.content").regex(keywordRegex, "i");
        }
        if (condition.getFromId() != null) {
            msgCriteria.and("messages.fromId").is(condition.getFromId());
        }
        if (condition.getBeforeDate() != null) {
            msgCriteria.and("messages.createdAt").lte(condition.getBeforeDate());
        }
        if (condition.getAfterDate() != null) {
            msgCriteria.and("messages.createdAt").gte(condition.getAfterDate());
        }

        // Aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(docCriteria), // Narrow to accessible channels
                Aggregation.unwind("messages"), // Unwind the messages array
                Aggregation.match(msgCriteria), // Filter messages
                Aggregation.replaceRoot("messages") // Return only the messages
        );

        AggregationResults<MessageDto> result = mongoTemplate.aggregate(aggregation,
                "channel_message_histories", MessageDto.class);
        return result.getMappedResults();
    }
}
