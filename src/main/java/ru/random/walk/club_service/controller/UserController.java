package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class UserController {
    @QueryMapping
    public List<ClubEntity> getUserClubs(
            @Argument UUID userId,
            @Argument PaginationInput pagination
    ) {
        log.info("""
                        Get user clubs
                        by user id [{}]
                        with pagination [{}]
                        """,
                userId, pagination
        );
        return Collections.singletonList(StubDataUtil.clubEntity());
    }

    @QueryMapping
    public List<AnswerEntity> getUserAnswers(
            @Argument UUID userId,
            @Argument PaginationInput pagination
    ) {
        log.info("""
                        Get user answers
                        by user id [{}]
                        with pagination [{}]
                        """,
                userId, pagination
        );
        return List.of(
                StubDataUtil.formAnswerEntity(),
                StubDataUtil.membersConfirmAnswerEntity()
        );
    }
}
