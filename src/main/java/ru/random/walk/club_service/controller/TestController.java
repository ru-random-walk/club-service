package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.mapper.UserMapper;
import ru.random.walk.club_service.service.UserService;
import ru.random.walk.dto.RegisteredUserInfoEvent;

import java.util.UUID;

@Controller
@AllArgsConstructor
@Slf4j
@PreAuthorize("hasRole('TESTER')")
public class TestController {
    private final UserMapper userMapper;
    private final UserService userService;

    @MutationMapping
    public UUID listenRegisteredUserInfoEvent(@Argument RegisteredUserInfoEvent event) {
        log.info("[TESTING] User was registered with event info: {}", event);
        var userEntity = userMapper.toEntity(event);
        userService.add(userEntity);
        return event.id();
    }
}
