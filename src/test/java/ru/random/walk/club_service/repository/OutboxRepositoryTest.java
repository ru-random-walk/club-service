package ru.random.walk.club_service.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.AbstractContainerTest;
import ru.random.walk.club_service.model.entity.OutboxMessage;
import ru.random.walk.dto.UserExcludeEvent;
import ru.random.walk.topic.EventTopic;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@AllArgsConstructor(onConstructor = @__(@Autowired))
class OutboxRepositoryTest extends AbstractContainerTest {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Transactional
    void testFindAllBySentFalse() throws JsonProcessingException {
        OutboxMessage outboxMessage = new OutboxMessage();
        var payload = objectMapper.writeValueAsString(UserExcludeEvent.builder()
                .userId(UUID.randomUUID())
                .clubId(UUID.randomUUID())
                .build());
        outboxMessage.setPayload(payload);
        outboxMessage.setTopic(EventTopic.USER_EXCLUDE);

        assertFalse(outboxMessage.isSent());

        outboxRepository.saveAndFlush(outboxMessage);

        var all = outboxRepository.findAllBySentFalse();
        assertEquals(1, all.size());
    }
}