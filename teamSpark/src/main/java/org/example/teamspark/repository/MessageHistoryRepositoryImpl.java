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

        Criteria criteria = new Criteria();
        if (condition.getSearchKeyword() != null && !condition.getSearchKeyword().isEmpty()) {
            String keywordRegex = ".*" + condition.getSearchKeyword() + ".*";
            criteria.and("messages.content").regex(keywordRegex, "i");
        }
        if (condition.getFromId() != null) {
            criteria.and("messages.fromId").is(condition.getFromId());
        }
        if (condition.getBeforeDate() != null) {
            criteria.and("messages.createdAt").lte(condition.getBeforeDate());
        }
        if (condition.getAfterDate() != null) {
            criteria.and("messages.createdAt").gte(condition.getAfterDate());
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.unwind("messages"), // Unwind the array to process each element individually
                Aggregation.match(criteria),   // Re-apply the condition to filter the specific messages
                Aggregation.replaceRoot("messages") // Return only the matching messages
        );

        AggregationResults<MessageDto> result = mongoTemplate.aggregate(aggregation, "channel_message_histories", MessageDto.class);
        return result.getMappedResults();
    }
}
