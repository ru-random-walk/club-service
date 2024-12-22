package ru.random.walk.club_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.UUID;

@Controller
@Slf4j
public class ClubController {
    @QueryMapping
    @Nullable ClubEntity getClub(@Argument UUID clubId) {
        log.info("""
                        Get club
                        by clubId [{}]
                        """,
                clubId
        );
        return StubDataUtil.clubEntity();
    }

    @MutationMapping
    ClubEntity createClub(@Argument String name) {
        log.info("""
                        Create club
                        with name [{}]
                        """,
                name
        );
        return StubDataUtil.clubEntityWith(name);
    }
}
