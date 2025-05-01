package ru.random.walk.club_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.AbstractContainerTest;
import ru.random.walk.club_service.model.entity.OutboxMessage;
import ru.random.walk.club_service.repository.OutboxRepository;
import ru.random.walk.club_service.service.OutboxSenderService;
import ru.random.walk.club_service.service.job.OutboxSendingJob;
import ru.random.walk.dto.CreatePrivateChatEvent;
import ru.random.walk.topic.EventTopic;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
class OutboxSenderServiceImplTest extends AbstractContainerTest {
    private final OutboxSenderService outboxSenderService;
    private final OutboxSendingJob outboxSendingJob;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final Scheduler scheduler;

    @Test
    @Transactional
    @Rollback
    void sendMessageSavesToDb() throws JsonProcessingException {
        UUID personId = UUID.randomUUID();
        UUID partnerId = UUID.randomUUID();
        outboxSenderService.sendMessage(EventTopic.CREATE_CHAT, new CreatePrivateChatEvent(personId, partnerId));
        var messages = outboxRepository.findAll();

        assertFalse(messages.isEmpty());
        assertEquals(EventTopic.CREATE_CHAT, messages.getLast().getTopic());
        assertEquals(getPayload(personId, partnerId), messages.getLast().getPayload());
        assertNotNull(messages.getLast().getCreatedAt());
    }

    @Test
    @Transactional
    @Rollback
    void checkOutboxJobIsSendingMessages() throws JsonProcessingException {
        String payload = getPayload(UUID.randomUUID(), UUID.randomUUID());

        OutboxMessage outboxMessage = new OutboxMessage();
        outboxMessage.setPayload(payload);
        outboxMessage.setTopic(EventTopic.CREATE_CHAT);

        assertFalse(outboxMessage.isSent());

        outboxMessage = outboxRepository.save(outboxMessage);

        outboxSendingJob.execute(null);

        var outboxResult = outboxRepository.findById(outboxMessage.getId()).orElseThrow();
        assertTrue(outboxResult.isSent());
    }

    private String getPayload(UUID member1, UUID member2) throws JsonProcessingException {
        return objectMapper.writeValueAsString(new CreatePrivateChatEvent(member1, member2));
    }

    @Test
    void checkJobsAreExist() throws SchedulerException {
        scheduler.checkExists(JobKey.jobKey("OutboxExpireJob"));
        scheduler.checkExists(JobKey.jobKey("OutboxSendingJob"));
    }
}