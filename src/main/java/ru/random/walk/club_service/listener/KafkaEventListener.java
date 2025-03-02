package ru.random.walk.club_service.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.mapper.UserMapper;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.dto.RegisteredUserInfoEvent;
import ru.random.walk.topic.EventTopic;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaEventListener {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @KafkaListener(topics = EventTopic.USER_REGISTRATION, containerFactory = "registeredUserInfoEventKafkaListenerContainerFactory")
    public void listenRegisteredUserInfoEvent(RegisteredUserInfoEvent event) {
        log.info("User was registered with event info: {}", event);
        var userEntity = userMapper.toEntity(event);
        userRepository.save(userEntity);
    }
}
