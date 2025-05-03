package ru.random.walk.club_service.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.mapper.ApprovementMapper;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.graphql.types.FormInput;
import ru.random.walk.club_service.model.graphql.types.MembersConfirmInput;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.service.ClubService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
@PreAuthorize("hasAuthority('DEFAULT_USER')")
public class ClubController {
    private final ApprovementMapper approvementMapper;
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
            @Argument @Nullable String description,
            Principal principal
    ) {
        log.info("""
                        Create club for [{}]
                        with login [{}]
                        with description [{}]
                        with name [{}]
                        """,
                principal, principal.getName(), description, name
        );
        var adminLogin = UUID.fromString(principal.getName());
        return clubService.createClub(name, description, adminLogin);
    }

    @MutationMapping
    public ClubEntity createClubWithMembersConfirmApprovement(
            @Argument String name,
            @Argument @Nullable String description,
            @Argument MembersConfirmInput membersConfirm,
            Principal principal
    ) {
        log.info("""
                        Create club for [{}]
                        with login [{}]
                        with description [{}]
                        with membersConfirm [{}]
                        with name [{}]
                        """,
                principal, principal.getName(), description, membersConfirm, name
        );
        var membersConfirmApprovementData = approvementMapper.toMembersConfirmApprovementData(membersConfirm);
        var adminLogin = UUID.fromString(principal.getName());
        return clubService.createClubWithApprovement(name, description, membersConfirmApprovementData, adminLogin);
    }

    @MutationMapping
    public ClubEntity createClubWithFormApprovement(
            @Argument String name,
            @Argument @Nullable String description,
            @Argument FormInput form,
            Principal principal
    ) {
        log.info("""
                        Create club for [{}]
                        with login [{}]
                        with description [{}]
                        with form [{}]
                        with name [{}]
                        """,
                principal, principal.getName(), description, form, name
        );
        var formApprovementData = approvementMapper.toFormApprovementData(form);
        var adminLogin = UUID.fromString(principal.getName());
        return clubService.createClubWithApprovement(name, description, formApprovementData, adminLogin);
    }

    @BatchMapping(typeName = "Club", field = "approversNumber", maxBatchSize = 30)
    public List<Integer> approversNumber(List<ClubEntity> clubs, Principal principal) {
        log.info("""
                        Batch approversNumber query for [{}]
                        with login [{}]
                        with clubs size [{}]
                        """,
                principal, principal.getName(), clubs.size()
        );
        return clubService.getClubToApproversNumber(clubs);
    }
}
