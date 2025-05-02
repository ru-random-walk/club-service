package ru.random.walk.club_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.AbstractContainerTest;
import ru.random.walk.club_service.model.entity.OutboxMessage;
import ru.random.walk.club_service.repository.OutboxRepository;
import ru.random.walk.club_service.service.job.OutboxSendingJob;
import ru.random.walk.dto.CreatePrivateChatEvent;
import ru.random.walk.dto.UserExcludeEvent;
import ru.random.walk.topic.EventTopic;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AllArgsConstructor(onConstructor = @__(@Autowired))
class OutboxSenderServiceTest extends AbstractContainerTest {
    private final OutboxSenderService outboxSenderService;
    private final OutboxSendingJob outboxSendingJob;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final Scheduler scheduler;

    @BeforeEach
    public void clearAll(){
        outboxRepository.deleteAll();
    }

    @Test
    @Transactional
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
    void sendAllMessagesSavesToDb() {
        var clubId = UUID.randomUUID();
        var eventCount = 100;
        List<UserExcludeEvent> allEvents = IntStream.range(0, eventCount)
                .mapToObj(ignored -> UserExcludeEvent.builder()
                        .clubId(clubId)
                        .userId(UUID.randomUUID())
                        .build())
                .toList();
        outboxSenderService.sendAllMessages(EventTopic.USER_EXCLUDE, allEvents);
        var messages = outboxRepository.findAll();

        assertFalse(messages.isEmpty());
        assertEquals(eventCount, messages.size());
        for (var message : messages) {
            assertEquals(EventTopic.USER_EXCLUDE, message.getTopic());
            var event = assertDoesNotThrow(() -> objectMapper.readValue(message.getPayload(), UserExcludeEvent.class));
            assertEquals(clubId, event.clubId());
            assertFalse(message.isSent());
            assertNotNull(messages.getLast().getCreatedAt());
        }
    }

    @Test
    @Transactional
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