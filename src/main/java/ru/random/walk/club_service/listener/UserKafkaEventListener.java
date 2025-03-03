package ru.random.walk.club_service.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.mapper.UserMapper;
import ru.random.walk.club_service.service.UserService;
import ru.random.walk.dto.RegisteredUserInfoEvent;
import ru.random.walk.topic.EventTopic;

@Service
@Slf4j
@AllArgsConstructor
public class UserKafkaEventListener {
    private final UserService userService;
    private final UserMapper userMapper;

    @KafkaListener(topics = EventTopic.USER_REGISTRATION, containerFactory = "registeredUserInfoEventKafkaListenerContainerFactory")
    public void listenRegisteredUserInfoEvent(RegisteredUserInfoEvent event) {
        log.info("User was registered with event info: {}", event);
        var userEntity = userMapper.toEntity(event);
        userService.add(userEntity);
    }
}
