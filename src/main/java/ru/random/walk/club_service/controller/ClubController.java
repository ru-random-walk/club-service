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
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.model.graphql.types.FormInput;
import ru.random.walk.club_service.model.graphql.types.MembersConfirmInput;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.model.graphql.types.PhotoInput;
import ru.random.walk.club_service.model.graphql.types.PhotoUrl;
import ru.random.walk.club_service.service.ClubService;
import ru.random.walk.club_service.service.auth.Authenticator;
import ru.random.walk.util.FileUtil;
import ru.random.walk.util.KeyRateLimiter;
import ru.random.walk.util.PathBuilder;

import java.io.IOException;
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
    private final Authenticator authenticator;
    private final KeyRateLimiter<UUID> uploadPhotoForClubRateLimiter;
    private final KeyRateLimiter<String> getClubPhotoUserRateLimiter;

    @QueryMapping
    public @Nullable ClubEntity getClub(
            @Argument UUID clubId,
            @Nullable @Argument PaginationInput membersPagination,
            Principal principal,
            DataFetchingEnvironment env
    ) {
        boolean membersIsRequired = env.getSelectionSet().contains("members");
        if (membersIsRequired) {
            authenticator.authAdminByClubId(principal, clubId);
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
        return clubService.getClubById(clubId, membersPagination, membersIsRequired);
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

    @MutationMapping
    public UUID removeClubWithAllItsData(
            @Argument UUID clubId,
            Principal principal
    ) {
        log.info("""
                        Remove club with all its data for [{}]
                        with login [{}]
                        with id [{}]
                        """,
                principal, principal.getName(), clubId
        );
        authenticator.authAdminByClubId(principal, clubId);
        return clubService.removeClubWithAllItsData(clubId);
    }

    @MutationMapping
    public PhotoUrl uploadPhotoForClub(
            @Argument UUID clubId,
            @Argument PhotoInput photo,
            Principal principal
    ) throws IOException {
        log.info("""
                        Upload club photo for [{}]
                        with login [{}]
                        with clubId [{}]
                        """,
                principal, principal.getName(), clubId
        );
        uploadPhotoForClubRateLimiter.throwIfRateLimitExceeded(clubId, () -> new ValidationException("Rate limit exceeded!"));
        authenticator.authAdminByClubId(principal, clubId);
        if (!FileUtil.isImage(photo.getBase64())) {
            throw new ValidationException("File is not image!");
        }
        var inputFile = FileUtil.getInputStream(photo.getBase64());
        return clubService.uploadPhotoForClub(clubId, inputFile);
    }

    @MutationMapping
    public ClubEntity removeClubPhoto(@Argument UUID clubId, Principal principal) {
        log.info("""
                        Remove club photo for [{}]
                        with login [{}]
                        with clubId [{}]
                        """,
                principal, principal.getName(), clubId
        );
        authenticator.authAdminByClubId(principal, clubId);
        return clubService.removeClubPhoto(clubId);
    }

    @QueryMapping
    public PhotoUrl getClubPhoto(
            @Argument UUID clubId,
            Principal principal
    ) {
        log.info("""
                        Get club photo for [{}]
                        with login [{}]
                        with clubId [{}]
                        """,
                principal, principal.getName(), clubId
        );
        var userId = UUID.fromString(principal.getName());
        getClubPhotoUserRateLimiter.throwIfRateLimitExceeded(
                PathBuilder.init()
                        .add(PathBuilder.Key.USER_ID, userId)
                        .add(PathBuilder.Key.CLUB_ID, clubId)
                        .build(),
                () -> new ValidationException("Rate limit exceeded!")
        );
        return clubService.getClubPhoto(clubId);
    }

    @QueryMapping
    public List<ClubEntity> searchClubs(
            @Argument String query,
            @Argument PaginationInput pagination,
            Principal principal
    ) {
        log.info("""
                        Search clubs for [{}]
                        with login [{}]
                        with query [{}]
                        """,
                principal, principal.getName(), query
        );
        pagination = Optional.ofNullable(pagination)
                .orElse(PaginationInput.newBuilder()
                        .page(0)
                        .size(30)
                        .build());
        return clubService.searchClubs(query, pagination);
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
