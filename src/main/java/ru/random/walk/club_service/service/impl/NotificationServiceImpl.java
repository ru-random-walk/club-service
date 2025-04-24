package ru.random.walk.club_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.model.NotificationAdditionalDataKey;
import ru.random.walk.club_service.model.model.NotificationTitle;
import ru.random.walk.club_service.service.NotificationService;
import ru.random.walk.dto.SendNotificationEvent;
import ru.random.walk.topic.EventTopic;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final static String EMPTY_NOTIFICATION_BODY = "";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void sendForAssignedApprover(UUID approverId, UUID answerer, UUID clubId) {
        try {
            var notificationEvent = SendNotificationEvent.builder()
                    .userId(approverId)
                    .title(NotificationTitle.APPROVER_WAS_ASSIGNED.name())
                    .body(EMPTY_NOTIFICATION_BODY)
                    .additionalData(Map.of(
                            NotificationAdditionalDataKey.ANSWERER.name(), answerer.toString(),
                            NotificationAdditionalDataKey.CLUB_ID.name(), clubId.toString()
                    ))
                    .build();
            kafkaTemplate.send(EventTopic.SEND_NOTIFICATION, objectMapper.writeValueAsString(notificationEvent));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
