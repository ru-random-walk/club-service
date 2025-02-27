package ru.random.walk.club_service.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.service.ClubService;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class ClubController {
    private final ClubService clubService;

    @QueryMapping
    public @Nullable ClubEntity getClub(
            @Argument UUID clubId,
            @Nullable @Argument PaginationInput membersPagination,
            Principal principal,
            DataFetchingEnvironment env
    ) {
        boolean membersIsRequired = env.getSelectionSet().contains("members");
        if (membersIsRequired) {
            membersPagination = Optional.ofNullable(membersPagination)
                    .orElse(new PaginationInput(0, 10));
        }
        log.info("""
                        Get club for [{}]
                        with login [{}]
                        by clubId [{}]
                        with members pagination [{}]
                        """,
                principal, principal.getName(), clubId, membersPagination
        );
        return clubService.getClubById(clubId, membersPagination, membersIsRequired, principal);
    }

    @MutationMapping
    public ClubEntity createClub(
            @Argument String name,
            Principal principal
    ) {
        log.info("""
                        Create club for [{}]
                        with login [{}]
                        with name [{}]
                        """,
                principal, principal.getName(), name
        );
        return clubService.createClub(name, principal);
    }
}
