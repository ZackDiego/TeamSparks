package org.example.teamspark.repository;

import org.example.teamspark.data.dto.SearchCondition;
import org.example.teamspark.data.dto.message.MessageDto;

import java.util.List;

public interface CustomMessageHistoryRepository {
    List<MessageDto> findMessagesWithSearchCondition(SearchCondition searchCondition);
}
