package ru.random.walk.club_service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.random.walk.dto.RegisteredUserInfoEvent;
import ru.random.walk.topic.EventTopic;

@Service
@Slf4j
public class KafkaEventListener {
    @KafkaListener(topics = EventTopic.USER_REGISTRATION, containerFactory = "registeredUserInfoEventKafkaListenerContainerFactory")
    public void listenRegisteredUserInfoEvent(RegisteredUserInfoEvent event) {
        log.info("User was registered with event info: {}", event);
        // TODO: save user
    }
}
