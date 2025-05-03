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
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.model.graphql.types.PhotoInput;
import ru.random.walk.club_service.model.graphql.types.PhotoUrl;
import ru.random.walk.club_service.service.ClubService;
import ru.random.walk.club_service.service.auth.Authenticator;
import ru.random.walk.util.FileUtil;

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
    private final ClubService clubService;
    private final Authenticator authenticator;

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
        return clubService.createClub(name, description, principal);
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
        authenticator.authAdminByClubId(principal, clubId);
        if (!FileUtil.isImage(photo.getBase64())) {
            throw new ValidationException("File is not image!");
        }
        var inputFile = FileUtil.getInputStream(photo.getBase64());
        return clubService.uploadPhotoForClub(clubId, inputFile);
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
        return clubService.getClubPhoto(clubId);
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
