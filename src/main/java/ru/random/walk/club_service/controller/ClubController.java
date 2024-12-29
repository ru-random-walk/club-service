package ru.random.walk.club_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.util.StubDataUtil;

import java.security.Principal;
import java.util.UUID;

@Controller
@Slf4j
public class ClubController {
    @QueryMapping
    @Nullable ClubEntity getClub(
            @Argument UUID clubId,
            @Argument PaginationInput membersPagination,
            Principal principal
    ) {
        log.info("""
                        Get club for [{}]
                        with login [{}]
                        by clubId [{}]
                        with members pagination [{}]
                        """,
                principal, principal.getName(), clubId, membersPagination
        );
        return StubDataUtil.clubEntity();
    }

    @MutationMapping
    ClubEntity createClub(@Argument String name, Principal principal) {
        log.info("""
                        Create club for [{}]
                        with login [{}]
                        with club name [{}]
                        """,
                principal, principal.getName(), name
        );
        return StubDataUtil.clubEntityWith(name);
    }
}
