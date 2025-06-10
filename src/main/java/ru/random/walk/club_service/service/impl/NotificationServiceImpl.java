package ru.random.walk.club_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.model.NotificationAdditionalDataKey;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.club_service.service.NotificationService;
import ru.random.walk.dto.SendNotificationEvent;
import ru.random.walk.topic.EventTopic;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    @Override
    public void sendForAssignedApprover(UUID approverId, UUID answerer, UUID clubId) {
        try {
            var clubName = clubRepository.findById(clubId)
                    .map(ClubEntity::getName)
                    .orElseThrow();
            var answererName = userRepository.findById(answerer)
                    .map(UserEntity::getFullName)
                    .orElseThrow();

            var notificationEvent = SendNotificationEvent.builder()
                    .userId(approverId)
                    .title("Подтверди заявку на вступление в группу '%s'!".formatted(clubName))
                    .body("Пользователь %s хочет вступить в группу '%s'!".formatted(answererName, clubName))
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
