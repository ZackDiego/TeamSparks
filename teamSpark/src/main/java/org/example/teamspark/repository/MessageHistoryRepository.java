package org.example.teamspark.repository;

import org.example.teamspark.data.dto.message.ChannelMessageHistoryDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageHistoryRepository extends MongoRepository<ChannelMessageHistoryDto, String>, CustomMessageHistoryRepository {
    ChannelMessageHistoryDto findOneByChannelId(Long channelId);

}
