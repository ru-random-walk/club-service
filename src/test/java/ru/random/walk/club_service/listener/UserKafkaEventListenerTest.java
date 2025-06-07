package ru.random.walk.club_service.listener;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.random.walk.club_service.AbstractContainerTest;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.dto.RegisteredUserInfoEvent;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AllArgsConstructor(onConstructor = @__(@Autowired))
class UserKafkaEventListenerTest extends AbstractContainerTest {
    private final UserKafkaEventListener eventListener;
    private final UserRepository userRepository;

    @Test
    void testListenRegisteredUserInfoEvent() {
        var userId = UUID.randomUUID();
        eventListener.listenRegisteredUserInfoEvent(RegisteredUserInfoEvent.builder()
                        .id(userId)
                        .fullName("Popup")
                .build());
        UserEntity user = userRepository.findById(userId).orElseThrow();
        assertEquals("Popup", user.getFullName());
    }

    @Test
    void testListenRegisteredUserInfoEventWithUpdatedInfo() {
        var userId = UUID.randomUUID();
        eventListener.listenRegisteredUserInfoEvent(RegisteredUserInfoEvent.builder()
                        .id(userId)
                        .fullName("Popup")
                .build());
        eventListener.listenRegisteredUserInfoEvent(RegisteredUserInfoEvent.builder()
                        .id(userId)
                        .fullName("Banner")
                .build());
        UserEntity user = userRepository.findById(userId).orElseThrow();
        assertEquals("Banner", user.getFullName());
    }

    @Test
    void testListenRegisteredUserInfoEventWithNotUpdatedInfo() {
        var userId = UUID.randomUUID();
        eventListener.listenRegisteredUserInfoEvent(RegisteredUserInfoEvent.builder()
                        .id(userId)
                        .fullName("Popup")
                .build());
        eventListener.listenRegisteredUserInfoEvent(RegisteredUserInfoEvent.builder()
                        .id(userId)
                        .fullName("Popup")
                .build());
        UserEntity user = userRepository.findById(userId).orElseThrow();
        assertEquals("Popup", user.getFullName());
    }
}