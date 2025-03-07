package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.service.Authenticator;
import ru.random.walk.club_service.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final Authenticator authenticator;

    @QueryMapping
    public Iterable<ClubEntity> getUserClubs(
            @Argument UUID userId,
            @Nullable @Argument PaginationInput pagination,
            Principal principal
    ) {
        log.info("""
                        Get user clubs for [{}]
                        with login [{}]
                        by user id [{}]
                        with pagination [{}]
                        """,
                principal, principal.getName(), userId, pagination
        );
        pagination = Optional.ofNullable(pagination).orElse(new PaginationInput(0, 20));
        return userService.getClubs(userId, pagination);
    }

    @QueryMapping
    public List<AnswerEntity> getUserAnswers(
            @Argument UUID userId,
            @Nullable @Argument PaginationInput pagination,
            Principal principal
    ) {
        log.info("""
                        Get user answers for [{}]
                        with login [{}]
                        by user id [{}]
                        with pagination [{}]
                        """,
                principal, principal.getName(), userId, pagination
        );
        authenticator.authUserById(userId, principal);
        pagination = Optional.ofNullable(pagination).orElse(new PaginationInput(0, 20));
        return userService.getAnswers(userId, pagination);
    }
}
